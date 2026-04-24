package com.dxc.tenantservice.application.dto.tenant.res;

import java.util.UUID;

public record RegisterTenantResDTO(
        UUID id,
        String name,
        String email,
        String website
) {
}
