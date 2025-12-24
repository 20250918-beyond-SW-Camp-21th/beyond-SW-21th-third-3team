package com.mukkebi.foodfinder.core.api.controller.v1.response;

public record AiRecommendResponse(
        String restaurantId,
        String restaurantName,
        String menu,
        String reason
) {
}
