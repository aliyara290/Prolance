package com.dxc.taskservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.taskservice.infrastructure.adapter.out.persistence.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepositoryJpa extends JpaRepository<TaskEntity, UUID> {
    Optional<TaskEntity> findByIdAndTenantId(UUID id, UUID tenantId);
    Page<TaskEntity> findAllByTenantId(UUID tenantId, Pageable pageable);
    Page<TaskEntity> findByProjectIdAndTenantId(UUID projectId, UUID tenantId, Pageable pageable);
    Page<TaskEntity> findByMilestoneIdAndTenantId(UUID milestoneId, UUID tenantId, Pageable pageable);
    
    @Query("SELECT t FROM TaskEntity t JOIN t.assignments a WHERE a.userId = :assigneeId AND t.tenantId = :tenantId AND a.status = 'ACTIVE'")
    Page<TaskEntity> findByAssigneeIdAndTenantId(UUID assigneeId, UUID tenantId, Pageable pageable);
    
    boolean existsByIdAndTenantId(UUID id, UUID tenantId);
}