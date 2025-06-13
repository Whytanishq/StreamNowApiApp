package com.streamnow.api.service;

import com.streamnow.api.dto.SubscriptionPlanDto;
import com.streamnow.api.entity.SubscriptionPlan;
import com.streamnow.api.exception.ResourceNotFoundException;
import com.streamnow.api.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public List<SubscriptionPlanDto> getAllActivePlans() {
        List<SubscriptionPlan> activePlans = subscriptionPlanRepository.findByActiveTrue();
        return activePlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SubscriptionPlanDto convertToDto(SubscriptionPlan plan) {
        return SubscriptionPlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .durationDays(plan.getDurationDays())
                .features(plan.getFeatures())
                .active(plan.getActive())
                .build();
    }

    public SubscriptionPlan getPlanById(Long planId) {
        return subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));
    }
}