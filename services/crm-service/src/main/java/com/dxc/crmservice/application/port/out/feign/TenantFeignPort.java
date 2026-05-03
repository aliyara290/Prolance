package com.dxc.crmservice.application.port.out.feign;

import java.util.UUID;

public interface TenantFeignPort {
    boolean isTenantActive(UUID id);
}
