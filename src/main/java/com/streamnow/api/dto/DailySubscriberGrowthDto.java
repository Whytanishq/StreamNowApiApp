// DailySubscriberGrowthDto.java
package com.streamnow.api.dto;

import lombok.Data;
import java.time.LocalDate;
import java.sql.Date;

@Data
public class DailySubscriberGrowthDto {
    private LocalDate date;
    private Long count;

    public DailySubscriberGrowthDto(Date date, Long count) {
        this.date = date.toLocalDate(); // Convert java.sql.Date to java.time.LocalDate
        this.count = count;
    }
}
