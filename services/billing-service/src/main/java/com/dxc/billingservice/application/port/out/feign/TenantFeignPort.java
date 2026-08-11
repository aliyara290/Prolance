package com.dxc.billingservice.application.port.out.feign;

import com.dxc.billingservice.domain.model.valueobject.EducationLevel;
import com.dxc.billingservice.domain.model.valueobject.SeniorityLevel;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

public interface TenantFeignPort {

    TenantUserDTO getUser(UUID userId);

    @Builder
    record TenantUserDTO(
            UUID id,
            UUID tenantId,
            String email,
            String firstName,
            String lastName,
            SeniorityLevel seniorityLevel,
            EducationLevel educationLevel,
            BigDecimal baseHourlySalary
    ) {
    }
}
