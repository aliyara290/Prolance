package com.dxc.tenantservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.tenantservice.domain.model.valueobject.Language;
import com.dxc.tenantservice.domain.model.tenant.TenantSettings;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.TenantSettingsEntity;
import org.springframework.stereotype.Component;

@Component
public class TenantSettingsPersistenceMapper {

    public TenantSettingsEntity domainToEntity(TenantSettings domain) {
        if (domain == null) return null;

        return TenantSettingsEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .plan(domain.getPlan())
                .planStatus(domain.getPlanStatus())
                .maxUsers(domain.getMaxUsers())
                .maxProjects(domain.getMaxProjects())
                .maxTasksPerProject(domain.getMaxTasksPerProject())
                .enableNotifications(domain.isEnableNotifications())
                .twoFactorRequired(domain.isTwoFactorRequired())
                .timezone(domain.getTimezone())
                .language(domain.getLanguage() != null ? domain.getLanguage().name() : null)
                .dateFormat(domain.getDateFormat())
                .build();
    }

    public TenantSettings entityToDomain(TenantSettingsEntity entity) {
        if (entity == null) return null;

        return new TenantSettings(
                entity.getId(),
                entity.getTenantId(),
                entity.getPlan(),
                entity.getPlanStatus(),
                entity.getMaxUsers(),
                entity.getMaxProjects(),
                entity.getMaxTasksPerProject(),
                entity.isEnableNotifications(),
                entity.isTwoFactorRequired(),
                entity.getTimezone(),
                entity.getLanguage() != null ? Language.valueOf(entity.getLanguage()) : null,
                entity.getDateFormat()
        );
    }
}
