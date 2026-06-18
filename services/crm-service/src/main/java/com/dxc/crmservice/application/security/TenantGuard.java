package com.dxc.crmservice.application.security;

import com.dxc.crmservice.application.port.out.feign.TenantFeignPort;
import com.dxc.crmservice.domain.exception.TenantNotActiveException;
import com.dxc.crmservice.domain.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TenantGuard {

    private final TenantFeignPort tenantFeignPort;

    public void ensureTenantIsActive(UUID tenantId) {
        if (tenantId == null) {
            throw new ValidationException("tenantId is required");
        }

        boolean active = tenantFeignPort.isTenantActive(tenantId);

        if (!active) {
            throw new TenantNotActiveException(tenantId);
        }
    }
}
