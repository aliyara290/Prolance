package com.dxc.tenantservice.application.dto.user.res;

import com.dxc.tenantservice.domain.model.enums.UserStatus;
import lombok.Builder;

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
        UserStatus status,
        LocalDateTime lastLoginAt,
        Set<UUID> keycloakRoleGroupIds
) {
}
