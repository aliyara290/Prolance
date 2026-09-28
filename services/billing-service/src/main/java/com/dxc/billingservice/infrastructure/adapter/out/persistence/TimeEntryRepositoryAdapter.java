package com.dxc.billingservice.infrastructure.adapter.out.persistence;

import com.dxc.billingservice.application.port.out.TimeEntryRepository;
import com.dxc.billingservice.domain.model.entity.TimeEntry;
import com.dxc.billingservice.infrastructure.persistence.entity.TimeEntryJpaEntity;
import com.dxc.billingservice.infrastructure.persistence.mapper.PersistenceMapper;
import com.dxc.billingservice.infrastructure.persistence.jpa.TimeEntryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TimeEntryRepositoryAdapter implements TimeEntryRepository {

    private final TimeEntryJpaRepository jpaRepository;
    private final PersistenceMapper mapper;

    @Override
    public TimeEntry save(TimeEntry entity) {
        TimeEntryJpaEntity savedEntity = jpaRepository.save(mapper.toTimeEntryJpaEntity(entity));
        return mapper.toTimeEntryDomain(savedEntity);
    }

    @Override
    public Optional<TimeEntry> findById(UUID id) {
        return jpaRepository.findById(id).map(entity -> mapper.toTimeEntryDomain(entity));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<TimeEntry> findByProjectIdAndDateRange(UUID projectId, LocalDateTime start, LocalDateTime end, UUID tenantId) {
        return jpaRepository.findByProjectIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndTenantId(projectId, start, end, tenantId)
                .stream()
                .map(entity -> mapper.toTimeEntryDomain(entity))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasOverlappingEntries(UUID userId, LocalDateTime start, LocalDateTime end, UUID tenantId) {
        return jpaRepository.existsOverlapping(userId, start, end, tenantId);
    }

    @Override
    public boolean hasOverlappingEntriesExcluding(UUID timeEntryId, UUID userId, LocalDateTime start, LocalDateTime end, UUID tenantId) {
        return jpaRepository.existsOverlappingExcluding(timeEntryId, userId, start, end, tenantId);
    }
}
