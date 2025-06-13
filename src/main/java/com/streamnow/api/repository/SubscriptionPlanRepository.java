package com.streamnow.api.repository;

import com.streamnow.api.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findByName(String name);
    List<SubscriptionPlan> findByActiveTrue();
}