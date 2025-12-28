package com.mukkebi.foodfinder.core.api.controller.v1.response;

import java.util.List;

public record UserReviewListResponse(
    List<ReviewResponse> reviews,
    Long nextCursor,
    boolean hasNext
){}
