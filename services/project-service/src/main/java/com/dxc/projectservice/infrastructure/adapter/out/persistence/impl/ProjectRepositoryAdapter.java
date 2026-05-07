package com.dxc.projectservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.projectservice.application.port.out.ProjectRepository;
import com.dxc.projectservice.domain.model.aggregate.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ProjectRepositoryAdapter implements ProjectRepository {

    @Override
    public Project save(Project entity) {
        return entity;
    }

    @Override
    public Optional<Project> findById(UUID id, UUID tenantId) {
        return Optional.empty();
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
    }

    @Override
    public Page<Project> findAll(UUID tenantId, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public boolean existsById(UUID id, UUID tenantId) {
        return false;
    }

    @Override
    public Page<Project> findByClientId(UUID clientId, UUID tenantId, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public Page<Project> findByProjectManagerId(UUID managerId, UUID tenantId, Pageable pageable) {
        return Page.empty();
    }
}
