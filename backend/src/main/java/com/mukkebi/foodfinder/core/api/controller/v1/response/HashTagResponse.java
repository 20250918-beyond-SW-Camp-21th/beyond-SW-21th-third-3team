package com.mukkebi.foodfinder.core.api.controller.v1.response;

import com.mukkebi.foodfinder.storage.HashTag;

public record HashTagResponse(
        Long id,
        String code,
        String label
) {
    public static HashTagResponse from(HashTag hashTag) {
        return new HashTagResponse(
                hashTag.getId(),
                hashTag.getCode(),
                hashTag.getLabel()
        );
    }
}
