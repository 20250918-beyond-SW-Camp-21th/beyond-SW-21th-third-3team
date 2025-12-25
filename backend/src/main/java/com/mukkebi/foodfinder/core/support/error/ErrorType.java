package com.mukkebi.foodfinder.core.support.error;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public enum ErrorType {

    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred.", LogLevel.ERROR),
    DEFAULT_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, ErrorCode.E400, "An unexpected error has occurred.", LogLevel.WARN),

    // 카카오 API 관련
    KAKAO_API_CLIENT_ERROR(HttpStatus.BAD_GATEWAY, ErrorCode.E502, "카카오 API 요청에 실패했습니다.", LogLevel.ERROR),
    KAKAO_API_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "카카오 API 인증에 실패했습니다.", LogLevel.ERROR),
    KAKAO_API_RATE_LIMIT(HttpStatus.TOO_MANY_REQUESTS, ErrorCode.E429, "카카오 API 호출 한도를 초과했습니다.", LogLevel.WARN),
    KAKAO_API_SERVER_ERROR(HttpStatus.BAD_GATEWAY, ErrorCode.E502, "카카오 API 서버 오류가 발생했습니다.", LogLevel.ERROR),
    KAKAO_API_CONNECTION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.E503, "카카오 API 연결에 실패했습니다.", LogLevel.ERROR);

    private final HttpStatus status;

    private final ErrorCode code;

    private final String message;

    private final LogLevel logLevel;

    ErrorType(HttpStatus status, ErrorCode code, String message, LogLevel logLevel) {

        this.status = status;
        this.code = code;
        this.message = message;
        this.logLevel = logLevel;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

}
