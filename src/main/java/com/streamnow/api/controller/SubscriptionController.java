package com.streamnow.api.controller;

import com.streamnow.api.dto.PaymentResponseDto;
import com.streamnow.api.dto.SubscriptionPlanDto;
import com.streamnow.api.dto.SubscriptionRequestDto;
import com.streamnow.api.entity.SubscriptionPlan;
import com.streamnow.api.service.JwtService;
import com.streamnow.api.service.PaymentService;
import com.streamnow.api.service.SubscriptionActivationService;
import com.streamnow.api.service.SubscriptionPlanService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionPlanService subscriptionPlanService;
    private final PaymentService paymentService;
    private final SubscriptionActivationService subscriptionActivationService;
    private final JwtService jwtService;


    @PostMapping("/free-trial")
    public ResponseEntity<Void> startFreeTrial(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        subscriptionActivationService.activateSubscription(userId, 1L, "FREE_TRIAL", "none");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanDto>> getAllPlans() {
        return ResponseEntity.ok(subscriptionPlanService.getAllActivePlans());
    }

    @PostMapping("/initiate-payment")
    public ResponseEntity<PaymentResponseDto> initiatePayment(
            @RequestHeader("Authorization") String token,
            @RequestBody SubscriptionRequestDto request) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(paymentService.initiatePayment(userId, request));
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<Void> verifyPayment(
            @RequestParam String sessionId,
            @RequestParam String transactionId) {
        paymentService.verifyPayment(sessionId, transactionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{planId}")
    public ResponseEntity<SubscriptionPlanDto> getPlanById(@PathVariable Long planId) {
        SubscriptionPlan plan = subscriptionPlanService.getPlanById(planId);
        return ResponseEntity.ok(subscriptionPlanService.convertToDto(plan));
    }


    private Long getUserIdFromToken(String token) {
        // Extract user ID from JWT
        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Claims claims = jwtService.getClaims(cleanToken);
        return Long.parseLong(claims.getSubject());
    }
}