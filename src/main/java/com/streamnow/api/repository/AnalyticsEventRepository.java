package com.streamnow.api.repository;

import com.streamnow.api.entity.AnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, Long> {

    long countByEventTypeAndCreatedAtBetween(String eventType, LocalDateTime start, LocalDateTime end);

    @Query("SELECT ae.contentId AS contentId, COUNT(ae.id) AS viewCount " +
            "FROM AnalyticsEvent ae WHERE ae.createdAt BETWEEN :start AND :end AND ae.eventType = 'CONTENT_VIEW' " +
            "GROUP BY ae.contentId ORDER BY viewCount DESC")
    List<Map<String, Object>> findMostWatchedContent(LocalDateTime start, LocalDateTime end);

    @Query("SELECT HOUR(ae.createdAt) AS hour, COUNT(ae.id) AS views " +
            "FROM AnalyticsEvent ae WHERE ae.createdAt BETWEEN :start AND :end " +
            "GROUP BY hour ORDER BY views DESC")
    List<Map<String, Object>> findPeakViewingHours(LocalDateTime start, LocalDateTime end);
}
