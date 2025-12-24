package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.request.ReviewRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ReviewResponse;
import com.mukkebi.foodfinder.core.domain.ReviewService;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    //후기 작성
    @PostMapping("/api/reviews/{id}")
    public ApiResult<?> postReview(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        reviewService.saveReview(reviewRequest, id, oauth2User);
        return ApiResult.success();
    }

    //후기 수정
    @PutMapping("/api/reviews/{id}")
    public ApiResult<?> updateReview(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        reviewService.updateReview(reviewRequest, id, oauth2User);
        return ApiResult.success();
    }

    //후기 삭제
    @DeleteMapping("/api/reviews/{id}")
    public ApiResult<?> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        reviewService.deleteReview(id, oauth2User);
        return ApiResult.success();
    }

    // 내 후기 조회
    @GetMapping("/api/users/me/reviews")
    public ApiResult<List<ReviewResponse>> getReviewsByUser(
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        return ApiResult.success(reviewService.getMyReviews(oauth2User));
    }

    //음식점별 후기 조회
    @GetMapping("/api/restaurants/reviews/{id}")
    public ApiResult<List<ReviewResponse>> getReviewsByRestaurant(@PathVariable Long id) {
        return ApiResult.success(reviewService.getByRestaurant(id));
    }

}
