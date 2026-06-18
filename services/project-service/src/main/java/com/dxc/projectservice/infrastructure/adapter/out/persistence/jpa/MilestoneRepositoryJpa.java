package com.dxc.projectservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.projectservice.domain.model.valueobject.MilestoneStatus;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.MilestoneEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MilestoneRepositoryJpa extends JpaRepository<MilestoneEntity, UUID> {
    
    List<MilestoneEntity> findByProjectIdAndTenantId(UUID projectId, UUID tenantId);
    List<MilestoneEntity> findByTenantIdAndDueDateBefore(UUID tenantId, LocalDateTime dueDate);

    Page<MilestoneEntity> findByTenantId(UUID tenantId, Pageable pageable);
    Page<MilestoneEntity> findByTenantIdAndStatus(UUID tenantId, MilestoneStatus status, Pageable pageable);
    
    Page<MilestoneEntity> findByTenantIdAndDueDateAfterAndStatusNot(UUID tenantId, LocalDateTime currentDate, MilestoneStatus status, Pageable pageable);
    Page<MilestoneEntity> findByTenantIdAndDueDateBeforeAndStatusNot(UUID tenantId, LocalDateTime currentDate, MilestoneStatus status, Pageable pageable);
    
    long countByTenantId(UUID tenantId);
    long countByTenantIdAndStatus(UUID tenantId, MilestoneStatus status);
    long countByTenantIdAndDueDateBeforeAndStatusNot(UUID tenantId, LocalDateTime currentDate, MilestoneStatus status);
}
