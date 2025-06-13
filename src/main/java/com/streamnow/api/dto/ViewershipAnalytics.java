package com.streamnow.api.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ViewershipAnalytics {
    private long totalViews;
    private List<Map<String, Object>> mostWatchedContent;
    private List<Map<String, Object>> peakViewingHours;
    private List<DailySubscriberGrowthDto> subscriberGrowth;
}
