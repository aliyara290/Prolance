package com.dxc.taskservice.domain.exception;

import java.util.UUID;

public class TenantNotActiveException extends RuntimeException {
    public TenantNotActiveException(UUID tenantId) {
        super("Tenant is not active: " + tenantId);
    }
}
