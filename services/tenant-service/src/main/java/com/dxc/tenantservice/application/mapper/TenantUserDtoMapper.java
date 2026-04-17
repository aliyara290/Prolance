package com.dxc.tenantservice.application.mapper;

import com.dxc.tenantservice.application.dto.user.req.TenantUserReqDto;
import com.dxc.tenantservice.domain.model.tenant.TenantUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantUserDtoMapper {
    TenantUser toDomain(TenantUserReqDto dto);
    TenantUserReqDto toDto(TenantUser domain);
}
