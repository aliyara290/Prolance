package com.dxc.tenantservice.domain.exception;

public class TenantValidationException extends DomainException {
    public TenantValidationException(String message) {
        super(message);
    }
}