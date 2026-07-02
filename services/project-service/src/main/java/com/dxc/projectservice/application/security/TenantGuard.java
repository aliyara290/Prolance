package com.dxc.projectservice.application.security;

import com.dxc.projectservice.application.port.out.feign.TenantFeignPort;
import com.dxc.projectservice.domain.exception.TenantNotActiveException;
import com.dxc.projectservice.domain.exception.ValidationException;
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
