package com.dxc.tenantservice.application.mapper;

import com.dxc.tenantservice.application.dto.tenant.req.AddressReqDTO;
import com.dxc.tenantservice.application.dto.tenant.res.AddressResponseDTO;
import com.dxc.tenantservice.domain.model.valueobject.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantAddressMapper {
    AddressResponseDTO toDto(Address address);
    Address toDomain(AddressReqDTO address);
}