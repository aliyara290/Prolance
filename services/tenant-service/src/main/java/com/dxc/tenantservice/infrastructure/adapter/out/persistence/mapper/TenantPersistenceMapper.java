package com.dxc.tenantservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.tenantservice.domain.model.tenant.Tenant;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.TenantEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TenantSettingsPersistenceMapper.class})
public interface TenantPersistenceMapper {
    TenantEntity domainToEntity(Tenant tenant);
    Tenant entityToDomain(TenantEntity tenantEntity);
}
