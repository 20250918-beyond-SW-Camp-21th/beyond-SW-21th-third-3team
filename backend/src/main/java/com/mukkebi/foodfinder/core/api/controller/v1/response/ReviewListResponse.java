package com.mukkebi.foodfinder.core.api.controller.v1.response;
import java.util.List;

public record ReviewListResponse(
        List<ReviewResponse> reviews,
        Double averageRating,
        Long nextCursor,
        boolean hasNext
) {}


