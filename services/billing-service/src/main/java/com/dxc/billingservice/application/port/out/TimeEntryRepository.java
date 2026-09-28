package com.dxc.billingservice.application.port.out;

import com.dxc.billingservice.domain.model.entity.TimeEntry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import java.util.Optional;

public interface TimeEntryRepository {
    
    TimeEntry save(TimeEntry entity);
    
    Optional<TimeEntry> findById(UUID id);
    
    void deleteById(UUID id);
    List<TimeEntry> findByProjectIdAndDateRange(UUID projectId, LocalDateTime start, LocalDateTime end, UUID tenantId);
    
    boolean hasOverlappingEntries(UUID userId, LocalDateTime start, LocalDateTime end, UUID tenantId);
    
    boolean hasOverlappingEntriesExcluding(UUID timeEntryId, UUID userId, LocalDateTime start, LocalDateTime end, UUID tenantId);
}
