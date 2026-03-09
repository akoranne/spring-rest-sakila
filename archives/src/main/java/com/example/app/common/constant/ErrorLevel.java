package com.example.app.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorLevel {
    INFO(0, "Info"),
    WARN(1, "Warn"),
    ERROR(2, "Error");

    private final Integer id;
    private final String name;
}
