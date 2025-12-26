package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.request.ReviewRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.ReviewListResponse;
import com.mukkebi.foodfinder.core.domain.ReaderService;
import com.mukkebi.foodfinder.core.domain.ReviewService;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReaderService readerService;

    //리뷰 작성
    @PostMapping("/reviews/{restaurantId}")
    public ApiResult<?> postReview(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        reviewService.saveReview(reviewRequest, restaurantId, oauth2User);
        return ApiResult.success();
    }

    //리뷰 수정
    @PutMapping("/reviews/{reviewId}")
    public ApiResult<?> updateReview(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        reviewService.updateReview(reviewRequest, reviewId, oauth2User);
        return ApiResult.success();
    }

    //리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ApiResult<?> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        reviewService.deleteReview(reviewId, oauth2User);
        return ApiResult.success();
    }

   //내 리뷰 조회
    @GetMapping("/reviews/me")
    public ApiResult<ReviewListResponse> getMyReviews(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestParam(required = false) Long cursorId
    ) {
        return ApiResult.success(
                readerService.getMyReviews(oauth2User, cursorId)
        );
    }


    //음식점 리뷰 조회
    @GetMapping("/restaurants/{restaurantId}/reviews")
    public ApiResult<ReviewListResponse> getReviewsByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Long cursorId
    ) {
        return ApiResult.success(
                readerService.getByRestaurant(restaurantId, cursorId)
        );
    }



}
