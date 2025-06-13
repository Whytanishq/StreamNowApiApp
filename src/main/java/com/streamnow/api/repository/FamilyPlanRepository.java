package com.streamnow.api.repository;

import com.streamnow.api.entity.FamilyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyPlanRepository extends JpaRepository<FamilyPlan, Long> {
    // Add custom query methods as needed
}