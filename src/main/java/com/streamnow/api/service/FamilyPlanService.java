package com.streamnow.api.service;

import com.streamnow.api.entity.*;
import com.streamnow.api.exception.ResourceNotFoundException;
import com.streamnow.api.repository.FamilyPlanRepository;
import com.streamnow.api.repository.SubscriptionPlanRepository;
import com.streamnow.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FamilyPlanService {
    private final FamilyPlanRepository familyPlanRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Transactional
    public FamilyPlan createFamilyPlan(Long primaryUserId, Long planId, Set<Long> memberIds) {
        User primaryUser = userRepository.findById(primaryUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Primary user not found"));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));

        Set<User> members = userRepository.findAllByIdIn(memberIds);

        LocalDateTime now = LocalDateTime.now();

        FamilyPlan familyPlan = FamilyPlan.builder()
                .primaryUser(primaryUser)
                .members(members)
                .plan(plan)
                .startDate(now)
                .endDate(now.plusDays(plan.getDurationDays()))
                .isActive(true)
                .build();

        return familyPlanRepository.save(familyPlan);
    }

    @Transactional
    public void deactivateFamilyPlan(Long familyPlanId) {
        FamilyPlan familyPlan = familyPlanRepository.findById(familyPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Family plan not found"));

        familyPlan.setIsActive(false);
        familyPlanRepository.save(familyPlan);
    }
}