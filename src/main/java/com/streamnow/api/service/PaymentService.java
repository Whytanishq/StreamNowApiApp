package com.streamnow.api.service;

import com.streamnow.api.dto.PaymentResponseDto;
import com.streamnow.api.dto.SubscriptionRequestDto;
import com.streamnow.api.entity.*;
import com.streamnow.api.exception.PaymentException;
import com.streamnow.api.repository.PaymentSessionRepository;
import com.streamnow.api.repository.SubscriptionPlanRepository;
import com.streamnow.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentSessionRepository paymentSessionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserRepository userRepository;
    private final SubscriptionActivationService subscriptionActivationService;
    @Value("${payment.session.timeout.minutes}")
    private int paymentSessionTimeoutMinutes;

    @Value("${payment.dummy.url}")
    private String dummyPaymentUrl;

    @Transactional
    public void verifyPayment(String sessionId, String transactionId) {
        PaymentSession session = paymentSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new PaymentException("Payment session not found"));

        if (!"pending".equals(session.getStatus())) {
            throw new PaymentException("Payment already processed");
        }

        if (LocalDateTime.now().isAfter(session.getExpiresAt())) {
            session.setStatus("expired");
            paymentSessionRepository.save(session);
            throw new PaymentException("Payment session expired");
        }

        session.setStatus("completed");
        session.setPaymentTransactionId(transactionId);
        paymentSessionRepository.save(session);

        // Use the new service to activate subscription
        subscriptionActivationService.activateSubscription(
                session.getUser().getId(),
                session.getPlan().getId(),
                transactionId,
                session.getPaymentMethod());
    }

    @Transactional
    public PaymentResponseDto initiatePayment(Long userId, SubscriptionRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PaymentException("User not found"));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new PaymentException("Subscription plan not found"));

        // Create payment session
        PaymentSession session = PaymentSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .user(user)
                .plan(plan)
                .amount(plan.getPrice())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plus(paymentSessionTimeoutMinutes, ChronoUnit.MINUTES))
                .status("pending")
                .paymentMethod(request.getPaymentMethod())
                .build();

        paymentSessionRepository.save(session);

        // Generate dummy payment URL
        String paymentUrl = String.format("%s?sessionId=%s&amount=%s",
                dummyPaymentUrl, session.getSessionId(), plan.getPrice());

        return PaymentResponseDto.builder()
                .sessionId(session.getSessionId())
                .paymentUrl(paymentUrl)
                .expiresAt(session.getExpiresAt())
                .status(session.getStatus())
                .build();
    }

    @Transactional
    public void initiateRecurringPayment(UserSubscription subscription) {
        // Create a payment request for the renewal
        SubscriptionRequestDto request = SubscriptionRequestDto.builder()
                .planId(subscription.getPlan().getId())
                .paymentMethod(subscription.getPaymentMethod())
                .build();

        // Process payment (this will create a new payment session)
        PaymentResponseDto paymentResponse = initiatePayment(
                subscription.getUser().getId(),
                request
        );

    }

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void expireOldSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(paymentSessionTimeoutMinutes);
        paymentSessionRepository.updateExpiredSessions(cutoff);
    }
}