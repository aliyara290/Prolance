package com.dxc.tenantservice.infrastructure.adapter.in.rest.response;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        T data,
        Object meta,
        Instant timestamp
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                data,
                null,
                Instant.now()
        );
    }

    public static <T> ApiResponse<T> success(T data, Object meta) {
        return new ApiResponse<>(
                true,
                data,
                meta,
                Instant.now()
        );
    }
}