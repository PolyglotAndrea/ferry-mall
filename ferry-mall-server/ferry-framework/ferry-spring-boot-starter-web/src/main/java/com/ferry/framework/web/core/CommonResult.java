package com.ferry.framework.web.core;

import java.time.Instant;

public record CommonResult<T>(int code, String message, T data, long timestamp) {
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, "success", data, Instant.now().toEpochMilli());
    }

    public static <T> CommonResult<T> failed(int code, String message) {
        return new CommonResult<>(code, message, null, Instant.now().toEpochMilli());
    }
}
