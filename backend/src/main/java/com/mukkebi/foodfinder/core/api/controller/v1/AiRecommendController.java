package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.request.AiRecommendRequest;
import com.mukkebi.foodfinder.core.api.controller.v1.response.AiRecommendResponse;
import com.mukkebi.foodfinder.core.domain.AiRecommendService;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiRecommendController {

    private final AiRecommendService aiRecommendService;

    @PostMapping("/api/v1/ai/recommend")
    public ApiResult<AiRecommendResponse> recommend(
            @Validated @RequestBody AiRecommendRequest request
    ) {
        return ApiResult.success(aiRecommendService.recommend(request));
    }
}
