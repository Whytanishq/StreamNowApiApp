package com.streamnow.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentAnalyticsDto {
    private long totalContent;
    private long moviesCount;
    private long showsCount;
    private Map<String, Long> genreDistribution;
    private double averageRating;
}