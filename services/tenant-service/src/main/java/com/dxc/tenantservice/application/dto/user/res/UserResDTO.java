package com.dxc.tenantservice.application.dto.user.res;

import com.dxc.tenantservice.domain.model.valueobject.UserStatus;
import com.dxc.tenantservice.domain.model.valueobject.SeniorityLevel;
import com.dxc.tenantservice.domain.model.valueobject.EducationLevel;
import lombok.Builder;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record UserResDTO(
        UUID id,
        UUID tenantId,
        UUID keycloakUserId,
        String email,
        String firstName,
        String lastName,
        String jobTitle,
        String department,
        SeniorityLevel seniorityLevel,
        EducationLevel educationLevel,
        BigDecimal baseHourlySalary,
        UserStatus status,
        LocalDateTime lastLoginAt,
        Set<UUID> keycloakRoleGroupIds,
        String avatarUrl
) {
}
