package com.dxc.taskservice.infrastructure.adapter.out.feign.dto;

public record ProjectValidationResponse(
        boolean allowed,
        String message
) {
}
