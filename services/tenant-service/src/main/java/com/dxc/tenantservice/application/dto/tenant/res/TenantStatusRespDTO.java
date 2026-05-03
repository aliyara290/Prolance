package com.dxc.tenantservice.application.dto.tenant.res;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TenantStatusRespDTO (
        UUID tenantId,
        String status,
        boolean active,
        boolean deleted,
        LocalDateTime deletedAt
) {
}
