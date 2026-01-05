package com.mukkebi.foodfinder.core.api.controller.v1.response;

import com.mukkebi.foodfinder.core.domain.Review;
import com.mukkebi.foodfinder.core.domain.User;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import com.mukkebi.foodfinder.storage.UserRepository;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        String content,
        Double rating,
        String userNickname,
        String restaurantName,
        LocalDateTime createdAt
) {
    public static ReviewResponse of(Review review, User user) {
        return new ReviewResponse(
                review.getId(),
                review.getContent(),
                review.getRating(),
                user.getNickname(),
                review.getRestaurantName(),
                review.getCreatedAt()
        );
    }
}

