package com.streamnow.api.service;

import com.streamnow.api.entity.SubscriptionPlan;
import com.streamnow.api.entity.User;
import com.streamnow.api.entity.UserSubscription;
import com.streamnow.api.exception.ResourceNotFoundException;
import com.streamnow.api.repository.SubscriptionPlanRepository;
import com.streamnow.api.repository.UserRepository;
import com.streamnow.api.repository.UserSubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionActivationService {
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

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
}