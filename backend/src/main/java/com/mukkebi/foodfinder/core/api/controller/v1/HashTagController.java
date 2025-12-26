package com.mukkebi.foodfinder.core.api.controller.v1;

import com.mukkebi.foodfinder.core.api.controller.v1.response.HashTagResponse;
import com.mukkebi.foodfinder.core.enums.MealType;
import com.mukkebi.foodfinder.core.support.response.ApiResult;
import com.mukkebi.foodfinder.storage.HashTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HashTagController {
    private final HashTagRepository hashTagRepository;

    @GetMapping("/api/v1/hashtags")
    public ApiResult<List<HashTagResponse>> getHashTagsByMealType(
            @RequestParam("mealType") MealType mealType
    ) {
        List<HashTagResponse> hashTags = hashTagRepository.findAllByMealType(mealType)
                .stream()
                .map(HashTagResponse::from)
                .toList();
        return ApiResult.success(hashTags);
    }
}
