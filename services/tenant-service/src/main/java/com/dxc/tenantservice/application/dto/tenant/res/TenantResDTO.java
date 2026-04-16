package com.dxc.tenantservice.application.dto.tenant.res;

import java.util.UUID;

public record TenantResDTO(
        UUID tenantId,
        UUID userId,
        String accessToken,
        String refreshToken
) {
}