package com.dxc.billingservice.application.port.out.feign;

import java.util.UUID;

public interface ProjectFeignPort {
    ProjectDetailsDTO getProject(UUID projectId);

    record ProjectDetailsDTO(
            UUID id,
            UUID tenantId,
            UUID clientId,
            String name,
            String billingType,
            String status
    ) {}
}
