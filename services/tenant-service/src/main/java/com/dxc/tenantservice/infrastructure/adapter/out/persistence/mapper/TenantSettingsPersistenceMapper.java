package com.dxc.tenantservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.tenantservice.domain.model.tenant.TenantSettings;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.TenantSettingsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantSettingsPersistenceMapper {
    TenantSettingsEntity domainToEntity(TenantSettings settings);
    TenantSettings entityToDomain(TenantSettingsEntity settingsEntity);
}
