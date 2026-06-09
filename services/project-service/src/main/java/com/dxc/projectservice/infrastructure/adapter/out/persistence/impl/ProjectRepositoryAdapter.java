package com.dxc.projectservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.projectservice.application.port.out.ProjectRepository;
import com.dxc.projectservice.domain.model.aggregate.Project;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.ProjectEntity;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.jpa.ProjectRepositoryJpa;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.mapper.ProjectPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProjectRepositoryAdapter implements ProjectRepository {

    private final ProjectRepositoryJpa repository;
    private final ProjectPersistenceMapper mapper;

    @Override
    public Project save(Project domain) {
        ProjectEntity project = mapper.toEntity(domain);
        return mapper.toDomain(repository.save(project));
    }

    @Override
    public Optional<Project> findById(UUID id, UUID tenantId) {
        return repository.findByIdAndTenantId(id, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        repository.findByIdAndTenantId(id, tenantId).ifPresent(repository::delete);
    }

    @Override
    public Page<Project> findAll(UUID tenantId, Pageable pageable) {
        return repository.findAllByTenantId(tenantId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id, UUID tenantId) {
        return repository.existsByIdAndTenantId(id, tenantId);
    }

    @Override
    public Page<Project> findByClientId(UUID clientId, UUID tenantId, Pageable pageable) {
        return repository.findByClientIdAndTenantId(clientId, tenantId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Project> findByProjectManagerId(UUID managerId, UUID tenantId, Pageable pageable) {
        return repository.findByProjectManagerIdAndTenantId(managerId, tenantId, pageable)
                .map(mapper::toDomain);
    }
}
