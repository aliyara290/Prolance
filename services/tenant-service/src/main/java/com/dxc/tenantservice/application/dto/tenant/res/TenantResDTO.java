package com.dxc.tenantservice.application.dto.tenant.res;

import com.dxc.tenantservice.domain.model.valueobject.TenantIndustry;
import com.dxc.tenantservice.domain.model.valueobject.TenantStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record TenantResDTO(
        UUID id,
        String name,
        String email,
        String website,
        Integer size,
        LocalDate foundedDate,
        String description,
        String logo,
        TenantIndustry industry,
        TenantStatus status,
        AddressResponseDTO address
) {
}