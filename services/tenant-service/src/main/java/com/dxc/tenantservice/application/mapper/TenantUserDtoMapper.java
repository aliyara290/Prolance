package com.dxc.tenantservice.application.mapper;

import com.dxc.tenantservice.application.dto.user.res.UserResDTO;
import com.dxc.tenantservice.domain.model.tenant.TenantUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantUserDtoMapper {
    UserResDTO toDto(TenantUser domain);
}
