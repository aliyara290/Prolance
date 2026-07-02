package com.dxc.tenantservice.application.dto.tenant.res;

public record AddressResponseDTO(
        String street,
        String city,
        String state,
        String country,
        String zipCode
) {
}
