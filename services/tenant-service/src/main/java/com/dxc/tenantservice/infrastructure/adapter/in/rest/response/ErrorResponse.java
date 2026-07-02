package com.dxc.tenantservice.infrastructure.adapter.in.rest.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        boolean success,
        ErrorDetail error,
        Instant timestamp
) {

    public static ErrorResponse of(String code, String message, List<String> details) {
        return new ErrorResponse(
                false,
                new ErrorDetail(code, message, details),
                Instant.now()
        );
    }

    public record ErrorDetail(
            String code,
            String message,
            List<String> details
    ) {}
}