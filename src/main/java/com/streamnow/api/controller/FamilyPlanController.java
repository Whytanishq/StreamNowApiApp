package com.streamnow.api.controller;

import com.streamnow.api.entity.FamilyPlan;
import com.streamnow.api.service.FamilyPlanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/family-plans")
@Tag(name = "Family Plans")
@RequiredArgsConstructor
public class FamilyPlanController {
    private final FamilyPlanService familyPlanService;

    @PostMapping
    public ResponseEntity<FamilyPlan> createFamilyPlan(
            @RequestParam Long primaryUserId,
            @RequestParam Long planId,
            @RequestBody Set<Long> memberIds) {
        return ResponseEntity.ok(
                familyPlanService.createFamilyPlan(primaryUserId, planId, memberIds)
        );
    }

    @DeleteMapping("/{familyPlanId}")
    public ResponseEntity<Void> deactivateFamilyPlan(
            @PathVariable Long familyPlanId) {
        familyPlanService.deactivateFamilyPlan(familyPlanId);
        return ResponseEntity.noContent().build();
    }
}