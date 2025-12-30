package com.mukkebi.foodfinder.core.api.controller.v1.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record StatisticsRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to
) {
    // Compact Constructor: 파라미터가 null일 경우 기본값 설정.
    public StatisticsRequest {
        if (to == null) {
            to = LocalDate.now();
        }
        if (from == null) {
            from = to.minusWeeks(1); // 기본값: 종료일 기준 1주일 전
        }
    }
}