package com.dxc.projectservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepositoryJpa extends JpaRepository<ProjectEntity, UUID> {
    Optional<ProjectEntity> findByIdAndTenantId(UUID id, UUID tenantId);
    Page<ProjectEntity> findAllByTenantId(UUID tenantId, Pageable pageable);
    Page<ProjectEntity> findByClientIdAndTenantId(UUID clientId, UUID tenantId, Pageable pageable);
    Page<ProjectEntity> findByProjectManagerIdAndTenantId(UUID managerId, UUID tenantId, Pageable pageable);
    boolean existsByIdAndTenantId(UUID id, UUID tenantId);
}
