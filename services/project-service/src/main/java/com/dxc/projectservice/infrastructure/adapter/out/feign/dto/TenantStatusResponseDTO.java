package com.dxc.projectservice.infrastructure.adapter.out.feign.dto;

import java.time.LocalDateTime;

public record TenantStatusResponseDTO(
        String status,
        boolean active,
        LocalDateTime deletedAt,
        boolean deleted
) {
}
