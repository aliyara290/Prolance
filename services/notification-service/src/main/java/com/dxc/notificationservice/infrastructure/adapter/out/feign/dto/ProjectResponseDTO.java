package com.dxc.notificationservice.infrastructure.adapter.out.feign.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProjectResponseDTO(
        UUID id,
        UUID tenantId,
        String name,
        String description,
        String status
) {
}
