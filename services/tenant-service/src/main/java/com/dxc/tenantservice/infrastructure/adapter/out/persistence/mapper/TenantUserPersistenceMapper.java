package com.dxc.tenantservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.tenantservice.domain.model.tenant.TenantUser;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.TenantUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TenantUserPersistenceMapper {

    TenantUserEntity domainToEntity(TenantUser tenantUser);

    TenantUser entityToDomain(TenantUserEntity tenantUserEntity);
}
