package com.dxc.projectservice.application.port.out;

import com.dxc.projectservice.domain.model.entity.Milestone;
import java.util.List;
import java.util.UUID;

public interface MilestoneRepository extends GenericRepository<Milestone> {
    List<Milestone> findByProjectId(UUID projectId, UUID tenantId);
    List<Milestone> findByTenantIdAndDueDateBefore(UUID tenantId, java.time.LocalDateTime date);
}
