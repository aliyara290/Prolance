package com.dxc.tenantservice.application.dto.tenant.res;

import com.dxc.tenantservice.domain.model.enums.TenantIndustry;
import com.dxc.tenantservice.domain.model.enums.TenantStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TenantResDTO(
        UUID id,
        String name,
        String email,
        String website,
        Integer size,
        java.time.LocalDate foundedDate,
        String description,
        String logo,
        TenantIndustry industry,
        TenantStatus status
) {
}