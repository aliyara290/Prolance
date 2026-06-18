package com.dxc.projectservice.infrastructure.adapter.out.feign.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record UserResponseDTO(
        UUID id,
        UUID tenantId,
        UUID keycloakUserId,
        String email,
        String firstName,
        String lastName,
        String jobTitle,
        String department,
        String status,
        LocalDateTime lastLoginAt,
        Set<UUID> keycloakRoleGroupIds
) {
}