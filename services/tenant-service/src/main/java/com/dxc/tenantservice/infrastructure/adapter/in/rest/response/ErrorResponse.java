package com.dxc.tenantservice.infrastructure.adapter.in.rest.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private final boolean success;
    private final ErrorDetail error;
    private final Instant timestamp;

    public static ErrorResponse of(String code, String message, List<String> details) {
        return ErrorResponse.builder()
                .success(false)
                .error(new ErrorDetail(code, message, details))
                .timestamp(Instant.now())
                .build();
    }

    @Getter
    @Builder
    public static class ErrorDetail {
        private String code;
        private String message;
        private List<String> details;
    }
}