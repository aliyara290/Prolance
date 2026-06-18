package com.dxc.projectservice.application.port.out;

import com.dxc.projectservice.domain.model.entity.Milestone;
import java.util.List;
import java.util.UUID;

public interface MilestoneRepository extends GenericRepository<Milestone> {
    List<Milestone> findByProjectId(UUID projectId, UUID tenantId);
    List<Milestone> findByTenantIdAndDueDateBefore(UUID tenantId, java.time.LocalDateTime date);
    
    org.springframework.data.domain.Page<Milestone> findByTenantId(UUID tenantId, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<Milestone> findByTenantIdAndStatus(UUID tenantId, com.dxc.projectservice.domain.model.valueobject.MilestoneStatus status, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<Milestone> findUpcomingMilestones(UUID tenantId, java.time.LocalDateTime currentDate, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<Milestone> findOverdueMilestones(UUID tenantId, java.time.LocalDateTime currentDate, org.springframework.data.domain.Pageable pageable);
    
    long countByTenantId(UUID tenantId);
    long countByTenantIdAndStatus(UUID tenantId, com.dxc.projectservice.domain.model.valueobject.MilestoneStatus status);
    long countOverdue(UUID tenantId, java.time.LocalDateTime currentDate);
}
