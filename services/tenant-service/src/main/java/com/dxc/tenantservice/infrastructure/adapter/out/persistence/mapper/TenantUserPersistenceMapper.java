package com.dxc.tenantservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.tenantservice.domain.model.tenant.TenantUser;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.TenantUserEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TenantUserPersistenceMapper {

    private final TenantUserPreferencePersistenceMapper preferenceMapper;

    public TenantUserPersistenceMapper(TenantUserPreferencePersistenceMapper preferenceMapper) {
        this.preferenceMapper = preferenceMapper;
    }

    public TenantUserEntity domainToEntity(TenantUser domain) {
        if (domain == null) return null;

        TenantUserEntity entity = TenantUserEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .keycloakUserId(domain.getKeycloakUserId())
                .keycloakRoleGroupIds(domain.getKeycloakRoleGroupIds())
                .email(domain.getEmail())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .jobTitle(domain.getJobTitle())
                .department(domain.getDepartment())
                .status(domain.getStatus())
                .lastLoginAt(domain.getLastLoginAt())
                .userPreference(preferenceMapper.domainToEntity(domain.getUserPreference()))
                .build();

        if (entity.getUserPreference() != null) {
            entity.getUserPreference().setTenantUserEntity(entity);
        }

        return entity;
    }

    public TenantUser entityToDomain(TenantUserEntity entity) {
        if (entity == null) return null;

        return new TenantUser(
                entity.getId(),
                entity.getTenantId(),
                entity.getKeycloakUserId(),
                entity.getKeycloakRoleGroupIds(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getJobTitle(),
                entity.getDepartment(),
                entity.getStatus(),
                entity.getLastLoginAt(),
                preferenceMapper.entityToDomain(entity.getUserPreference())
        );
    }
}
