package com.dxc.crmservice.application.utils;

import com.dxc.crmservice.domain.exception.DomainException;
import com.dxc.crmservice.infrastructure.config.TenantContextHolder;

import java.util.UUID;

public class Utils {

    public static UUID resolveTenantId() {
        String tenantIdStr = TenantContextHolder.getTenantId();
        if (tenantIdStr == null || tenantIdStr.isBlank()) {
            throw new DomainException("Tenant context not available. Authentication required.");
        }
        return UUID.fromString(tenantIdStr);
    }
}
