package com.dxc.taskservice.application.security;

import com.dxc.taskservice.application.port.out.feign.TenantFeignPort;
import com.dxc.taskservice.domain.exception.TenantNotActiveException;
import com.dxc.taskservice.domain.exception.ValidationException;
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
