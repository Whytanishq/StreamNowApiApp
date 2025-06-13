package com.streamnow.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationDays;
    private Boolean active;
    private List<String> features;
}