package com.dxc.projectservice.application.port.out;

import com.dxc.projectservice.domain.model.aggregate.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends GenericRepository<Project> {
    Page<Project> findByClientId(UUID clientId, UUID tenantId, Pageable pageable);
    Page<Project> findByProjectManagerId(UUID managerId, UUID tenantId, Pageable pageable);
    Optional<Project> findById(UUID id, UUID tenantId);
}
