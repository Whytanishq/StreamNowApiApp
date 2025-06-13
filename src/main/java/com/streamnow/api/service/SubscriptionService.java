package com.streamnow.api.service;

import com.streamnow.api.dto.PaymentResponseDto;
import com.streamnow.api.dto.SubscriptionRequestDto;
import com.streamnow.api.entity.*;
import com.streamnow.api.exception.ResourceNotFoundException;
import com.streamnow.api.exception.SubscriptionException;
import com.streamnow.api.repository.SubscriptionPlanRepository;
import com.streamnow.api.repository.UserRepository;
import com.streamnow.api.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PaymentService paymentService;
    private final SubscriptionActivationService subscriptionActivationService;

    @Transactional
    public void activateSubscription(Long userId, Long planId, String transactionId, String paymentMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));

        // Deactivate any existing subscription
        userSubscriptionRepository.deactivateUserSubscriptions(userId);

        // Create new subscription
        LocalDateTime now = LocalDateTime.now();
        UserSubscription subscription = UserSubscription.builder()
                .user(user)
                .plan(plan)
                .startDate(now)
                .endDate(now.plusDays(plan.getDurationDays()))
                .isActive(true)
                .paymentTransactionId(transactionId)
                .paymentMethod(paymentMethod)
                .build();

        userSubscriptionRepository.save(subscription);

        // Update user's JWT claims
        jwtService.updateUserClaims(user);
    }

    public boolean hasActiveSubscription(Long userId) {
        return userSubscriptionRepository.existsByUserIdAndIsActiveTrue(userId);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkExpiredSubscriptions() {
        List<UserSubscription> expiredSubscriptions = userSubscriptionRepository
                .findByIsActiveTrueAndEndDateBefore(LocalDateTime.now());

        expiredSubscriptions.forEach(sub -> {
            sub.setIsActive(false);
            userSubscriptionRepository.save(sub);
            jwtService.updateUserClaims(sub.getUser());
        });
    }

    @Transactional
    public void startFreeTrial(Long userId) {
        if (userSubscriptionRepository.existsByUserId(userId)) {
            throw new SubscriptionException("Free trial already used");
        }

        SubscriptionPlan freeTrial = subscriptionPlanRepository.findByName("Free Trial")
                .orElseThrow(() -> new ResourceNotFoundException("Free trial plan not configured"));

        subscriptionActivationService.activateSubscription(userId, freeTrial.getId(), "FREE_TRIAL", "none");
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void processRecurringPayments() {
        List<UserSubscription> renewals = userSubscriptionRepository
                .findByIsActiveTrueAndEndDateBetween(
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1)
                );

        renewals.forEach(sub -> {
            // Create a new payment request
            SubscriptionRequestDto request = SubscriptionRequestDto.builder()
                    .planId(sub.getPlan().getId())
                    .paymentMethod(sub.getPaymentMethod())
                    .build();

            // Process payment directly without circular dependency
            PaymentResponseDto paymentResponse = paymentService.initiatePayment(
                    sub.getUser().getId(),
                    request
            );

            // Verify payment with a recurring transaction ID
            paymentService.verifyPayment(
                    paymentResponse.getSessionId(),
                    "RECUR_" + sub.getPaymentTransactionId()
            );
        });
    }
}