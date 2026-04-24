package com.dxc.tenantservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.tenantservice.domain.model.tenant.UserPreference;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.TenantUserPreferenceEntity;
import org.springframework.stereotype.Component;

@Component
public class TenantUserPreferencePersistenceMapper {

    public TenantUserPreferenceEntity domainToEntity(UserPreference domain) {
        if (domain == null) return null;

        return TenantUserPreferenceEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .userId(domain.getUserId())
                .language(domain.getLanguage())
                .timezone(domain.getTimezone())
                .theme(domain.getTheme())
                .build();
    }

    public UserPreference entityToDomain(TenantUserPreferenceEntity entity) {
        if (entity == null) return null;

        return new UserPreference(
                entity.getId(),
                entity.getTenantId(),
                entity.getUserId(),
                entity.getLanguage(),
                entity.getTimezone(),
                entity.getTheme()
        );
    }
}
