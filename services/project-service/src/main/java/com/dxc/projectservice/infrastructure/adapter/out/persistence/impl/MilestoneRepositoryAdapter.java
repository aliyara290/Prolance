package com.dxc.projectservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.projectservice.application.port.out.MilestoneRepository;
import com.dxc.projectservice.domain.model.entity.Milestone;
import com.dxc.projectservice.domain.model.valueobject.MilestoneStatus;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.jpa.MilestoneRepositoryJpa;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.mapper.MilestonePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MilestoneRepositoryAdapter implements MilestoneRepository {

    private final MilestoneRepositoryJpa milestoneRepositoryJpa;
    private final MilestonePersistenceMapper mapper;

    @Override
    public List<Milestone> findByProjectId(UUID projectId, UUID tenantId) {
        return mapper.toDomainList(milestoneRepositoryJpa.findByProjectIdAndTenantId(projectId, tenantId));
    }

    @Override
    public List<Milestone> findByTenantIdAndDueDateBefore(UUID tenantId, LocalDateTime date) {
        return mapper.toDomainList(milestoneRepositoryJpa.findByTenantIdAndDueDateBefore(tenantId, date));
    }

    @Override
    public Page<Milestone> findByTenantId(UUID tenantId, Pageable pageable) {
        return milestoneRepositoryJpa.findByTenantId(tenantId, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Milestone> findByTenantIdAndStatus(UUID tenantId, MilestoneStatus status, Pageable pageable) {
        return milestoneRepositoryJpa.findByTenantIdAndStatus(tenantId, status, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Milestone> findUpcomingMilestones(UUID tenantId, LocalDateTime currentDate, Pageable pageable) {
        return milestoneRepositoryJpa.findByTenantIdAndDueDateAfterAndStatusNot(tenantId, currentDate, MilestoneStatus.COMPLETED, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Milestone> findOverdueMilestones(UUID tenantId, LocalDateTime currentDate, Pageable pageable) {
        return milestoneRepositoryJpa.findByTenantIdAndDueDateBeforeAndStatusNot(tenantId, currentDate, MilestoneStatus.COMPLETED, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public long countByTenantId(UUID tenantId) {
        return milestoneRepositoryJpa.countByTenantId(tenantId);
    }

    @Override
    public long countByTenantIdAndStatus(UUID tenantId, MilestoneStatus status) {
        return milestoneRepositoryJpa.countByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public long countOverdue(UUID tenantId, LocalDateTime currentDate) {
        return milestoneRepositoryJpa.countByTenantIdAndDueDateBeforeAndStatusNot(tenantId, currentDate, MilestoneStatus.COMPLETED);
    }

    @Override
    public Milestone save(Milestone entity) {
        var savedEntity = milestoneRepositoryJpa.save(mapper.toEntity(entity));
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Milestone> findById(UUID id, UUID tenantId) {
        return milestoneRepositoryJpa.findById(id)
                .filter(entity -> entity.getTenantId().equals(tenantId))
                .map(mapper::toDomain);
    }

    @Override
    public Page<Milestone> findAll(UUID tenantId, Pageable pageable) {
        return milestoneRepositoryJpa.findByTenantId(tenantId, pageable).map(mapper::toDomain);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        milestoneRepositoryJpa.findById(id).ifPresent(entity -> {
            if (entity.getTenantId().equals(tenantId)) {
                milestoneRepositoryJpa.delete(entity);
            }
        });
    }

    @Override
    public boolean existsById(UUID id, UUID tenantId) {
        return milestoneRepositoryJpa.findById(id)
                .map(entity -> entity.getTenantId().equals(tenantId))
                .orElse(false);
    }
}
