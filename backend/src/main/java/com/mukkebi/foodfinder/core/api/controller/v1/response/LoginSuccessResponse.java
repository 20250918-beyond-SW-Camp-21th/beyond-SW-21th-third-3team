package com.mukkebi.foodfinder.core.api.controller.v1.response;

import com.mukkebi.foodfinder.core.enums.UserStatus;

public record LoginSuccessResponse(
        Long userId,
        UserStatus status
) {}
