package com.streamnow.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionRequestDto {
    private Long planId;
    private String paymentMethod; // "upi", "card"
}
