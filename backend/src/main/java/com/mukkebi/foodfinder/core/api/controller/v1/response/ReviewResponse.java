package com.mukkebi.foodfinder.core.api.controller.v1.response;
public record ReviewResponse(
        String content,
        Double rating,
        String userNickname,
        Long restaurantId
) {}