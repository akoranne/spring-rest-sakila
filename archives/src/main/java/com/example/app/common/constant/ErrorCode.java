package com.example.app.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    GENERIC_ERROR("CODE-0001", ErrorLevel.ERROR, HttpStatus.INTERNAL_SERVER_ERROR, "Generic error"),
    GENERIC_WARN("CODE-0002", ErrorLevel.WARN, HttpStatus.INTERNAL_SERVER_ERROR, "Generic warning"),
    RESOURCE_NOT_FOUND("RESOURCE-0001", ErrorLevel.ERROR, HttpStatus.NOT_FOUND, "Resource not found"),
    RESOURCE_NOT_AVAILABLE("RESOURCE-0002", ErrorLevel.WARN, HttpStatus.INTERNAL_SERVER_ERROR, "Resource not available");

    private final String code;
    private final ErrorLevel level;
    private final HttpStatus httpStatus;
    private final String phrase;
}
