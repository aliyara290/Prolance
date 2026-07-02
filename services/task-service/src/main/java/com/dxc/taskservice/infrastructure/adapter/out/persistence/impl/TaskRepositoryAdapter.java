package com.dxc.taskservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.taskservice.application.port.out.TaskRepository;
import com.dxc.taskservice.domain.model.aggregate.Task;
import com.dxc.taskservice.infrastructure.adapter.out.persistence.entity.TaskEntity;
import com.dxc.taskservice.infrastructure.adapter.out.persistence.jpa.TaskRepositoryJpa;
import com.dxc.taskservice.infrastructure.adapter.out.persistence.mapper.TaskPersistenceMapper;
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
public class TaskRepositoryAdapter implements TaskRepository {

    private final TaskRepositoryJpa repository;
    private final TaskPersistenceMapper mapper;

    @Override
    public Task save(Task domain) {
        TaskEntity entity = mapper.toEntity(domain);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Task> findById(UUID id, UUID tenantId) {
        return repository.findByIdAndTenantId(id, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        repository.findByIdAndTenantId(id, tenantId).ifPresent(repository::delete);
    }

    @Override
    public Page<Task> findAll(UUID tenantId, Pageable pageable) {
        return repository.findAllByTenantId(tenantId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id, UUID tenantId) {
        return repository.existsByIdAndTenantId(id, tenantId);
    }

    @Override
    public Page<Task> findByProjectId(UUID projectId, UUID tenantId, Pageable pageable) {
        return repository.findByProjectIdAndTenantId(projectId, tenantId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Task> findByMilestoneId(UUID milestoneId, UUID tenantId, Pageable pageable) {
        return repository.findByMilestoneIdAndTenantId(milestoneId, tenantId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Task> findByAssigneeId(UUID assigneeId, UUID tenantId, Pageable pageable) {
        return repository.findByAssigneeIdAndTenantId(assigneeId, tenantId, pageable)
                .map(mapper::toDomain);
    }
}
