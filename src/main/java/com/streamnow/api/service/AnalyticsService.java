package com.streamnow.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamnow.api.dto.ViewershipAnalytics;
import com.streamnow.api.entity.AnalyticsEvent;
import com.streamnow.api.repository.AnalyticsEventRepository;
import com.streamnow.api.repository.ContentRepository;
import com.streamnow.api.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsEventRepository eventRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    private final ContentRepository contentRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ViewershipAnalytics getViewershipAnalytics(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        ViewershipAnalytics analytics = new ViewershipAnalytics();

        analytics.setTotalViews(
                eventRepository.countByEventTypeAndCreatedAtBetween(
                        "CONTENT_VIEW",
                        startDate.atStartOfDay(),
                        endDate.plusDays(1).atStartOfDay()
                )
        );

        analytics.setMostWatchedContent(
                eventRepository.findMostWatchedContent(
                        startDate.atStartOfDay(),
                        endDate.plusDays(1).atStartOfDay()
                )
        );

        analytics.setPeakViewingHours(
                eventRepository.findPeakViewingHours(
                        startDate.atStartOfDay(),
                        endDate.plusDays(1).atStartOfDay()
                )
        );

        analytics.setSubscriberGrowth(
                subscriptionRepository.countNewSubscribersByDate(
                        startDate.atStartOfDay(),
                        endDate.plusDays(1).atStartOfDay()
                )
        );

        return analytics;
    }

    public void trackEvent(String eventType, Long userId, String contentId,
                           String deviceId, String ipAddress, String userAgent,
                           Map<String, Object> eventData) {

        AnalyticsEvent event = new AnalyticsEvent();
        event.setEventType(eventType);
        event.setUserId(userId);
        event.setContentId(contentId);
        event.setDeviceId(deviceId);
        event.setIpAddress(ipAddress);
        event.setUserAgent(userAgent);

        try {
            event.setEventData(objectMapper.writeValueAsString(eventData));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize eventData", e);
        }

        eventRepository.save(event);
    }
}
