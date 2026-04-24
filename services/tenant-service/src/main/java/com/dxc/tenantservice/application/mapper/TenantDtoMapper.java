package com.dxc.tenantservice.application.mapper;

import com.dxc.tenantservice.application.dto.tenant.req.TenantReqDto;
import com.dxc.tenantservice.application.dto.tenant.res.TenantResDTO;
import com.dxc.tenantservice.domain.model.tenant.Tenant;
import com.dxc.tenantservice.domain.model.tenant.TenantUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantDtoMapper {
    Tenant toDomain(TenantReqDto dto);
    TenantResDTO toDto(Tenant domain);
}
