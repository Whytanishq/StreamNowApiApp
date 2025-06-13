package com.streamnow.api.repository;

import com.streamnow.api.dto.DailySubscriberGrowthDto;
import com.streamnow.api.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE UserSubscription us SET us.isActive = false WHERE us.user.id = :userId AND us.isActive = true")
    void deactivateUserSubscriptions(Long userId);

    @Query("SELECT new com.streamnow.api.dto.DailySubscriberGrowthDto(" +
            "FUNCTION('DATE', us.startDate), COUNT(us)) " +
            "FROM UserSubscription us " +
            "WHERE us.startDate BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE', us.startDate)")
    List<DailySubscriberGrowthDto> countNewSubscribersByDate(LocalDateTime start, LocalDateTime end);



    boolean existsByUserIdAndIsActiveTrue(Long userId);

    List<UserSubscription> findByIsActiveTrueAndEndDateBefore(LocalDateTime date);

    boolean existsByUserId(Long userId);

    List<UserSubscription> findByIsActiveTrueAndEndDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}