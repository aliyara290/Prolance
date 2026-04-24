package com.dxc.tenantservice.application.dto.tenant.res;

import lombok.Builder;

@Builder
public record TenantTokenResDTO(
        String accessToken,
        String refreshToken,
        Integer expiresIn,
        String tokenType
) {

}
