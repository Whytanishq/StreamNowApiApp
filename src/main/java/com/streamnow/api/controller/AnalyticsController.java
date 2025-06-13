package com.streamnow.api.controller;

import com.streamnow.api.dto.ViewershipAnalytics;
import com.streamnow.api.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/viewership")
    public ViewershipAnalytics getAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return analyticsService.getViewershipAnalytics(startDate, endDate);
    }

    @PostMapping("/track")
    public ResponseEntity<String> trackUserEvent(@RequestBody Map<String, Object> payload) {
        analyticsService.trackEvent(
                (String) payload.get("eventType"),
                ((Number) payload.get("userId")).longValue(),
                (String) payload.get("contentId"),
                (String) payload.get("deviceId"),
                (String) payload.get("ipAddress"),
                (String) payload.get("userAgent"),
                (Map<String, Object>) payload.get("eventData")
        );
        return ResponseEntity.ok("Event tracked successfully");
    }
}


