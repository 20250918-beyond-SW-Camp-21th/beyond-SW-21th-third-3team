package com.mukkebi.foodfinder.core.api.controller.v1.response;

public record HomeStatisticsResponse(
        long reviewCount,
        long visitCount,
        long recommendCount) {
}
