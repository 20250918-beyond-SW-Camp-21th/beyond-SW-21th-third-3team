package com.mukkebi.foodfinder.core.api.controller.v1.request;

import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Setter
public class StatisticsRequest {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;

    public LocalDate getTo() {
        if (to == null) {
            return LocalDate.now();
        }
        return to;
    }

    public LocalDate getFrom() {
        if (from == null) {
            return getTo().minusWeeks(1);
        }
        return from;
    }
}