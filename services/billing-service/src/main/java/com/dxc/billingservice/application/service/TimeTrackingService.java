package com.dxc.billingservice.application.service;

import com.dxc.billingservice.application.dto.req.LogTimeRequest;
import com.dxc.billingservice.application.dto.res.TimeEntryResponse;
import com.dxc.billingservice.application.mapper.BillingMapper;
import com.dxc.billingservice.application.port.in.TimeTrackingUseCase;
import com.dxc.billingservice.application.port.out.TimeEntryRepository;
import com.dxc.billingservice.domain.exception.BusinessRuleException;
import com.dxc.billingservice.domain.exception.OverlappingTimeEntryException;
import com.dxc.billingservice.domain.model.entity.TimeEntry;
import com.dxc.billingservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TimeTrackingService implements TimeTrackingUseCase {

    private final TimeEntryRepository timeEntryRepository;
    private final BillingMapper mapper;

    @Override
    @Transactional
    public TimeEntryResponse logTime(LogTimeRequest request) {
        UUID tenantId = getTenantId();
        UUID userId = getUserId();

        log.info("User {} logging time for project {}", userId, request.projectId());

        if (timeEntryRepository.hasOverlappingEntries(userId, request.startTime(), request.endTime(), tenantId)) {
            throw new OverlappingTimeEntryException("Time entry overlaps with an existing entry");
        }

        TimeEntry entry = TimeEntry.create(
                tenantId,
                request.projectId(),
                userId,
                request.taskId(),
                request.startTime(),
                request.endTime(),
                request.description(),
                request.billable(),
                userId
        );

        TimeEntry saved = timeEntryRepository.save(entry);
        return mapper.toTimeEntryResponse(saved);
    }

    @Override
    @Transactional
    public TimeEntryResponse updateTimeEntry(UUID timeEntryId, LogTimeRequest request) {
        UUID tenantId = getTenantId();
        UUID userId = getUserId();

        TimeEntry entry = timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new BusinessRuleException("Time entry not found"));

        if (!entry.getTenantId().equals(tenantId) || !entry.getUserId().equals(userId)) {
            throw new BusinessRuleException("You do not have permission to update this time entry");
        }

        if (timeEntryRepository.hasOverlappingEntriesExcluding(timeEntryId, userId, request.startTime(), request.endTime(), tenantId)) {
            throw new OverlappingTimeEntryException("Updated time entry overlaps with an existing entry");
        }

        entry.update(request.startTime(), request.endTime(), request.description(), request.billable());

        TimeEntry saved = timeEntryRepository.save(entry);
        return mapper.toTimeEntryResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTimeEntry(UUID timeEntryId) {
        UUID tenantId = getTenantId();
        UUID userId = getUserId();

        TimeEntry entry = timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new BusinessRuleException("Time entry not found"));

        if (!entry.getTenantId().equals(tenantId) || !entry.getUserId().equals(userId)) {
            throw new BusinessRuleException("You do not have permission to delete this time entry");
        }

        timeEntryRepository.deleteById(timeEntryId);
    }

    @Override
    public List<TimeEntryResponse> getProjectTimeEntries(UUID projectId, LocalDateTime start, LocalDateTime end) {
        UUID tenantId = getTenantId();
        
        List<TimeEntry> entries = timeEntryRepository.findByProjectIdAndDateRange(projectId, start, end, tenantId);
        
        return entries.stream()
                .map(mapper::toTimeEntryResponse)
                .collect(Collectors.toList());
    }

    private UUID getTenantId() {
        String tenantIdStr = TenantContextHolder.getTenantId();
        if (tenantIdStr == null) throw new BusinessRuleException("Tenant ID not found in context");
        return UUID.fromString(tenantIdStr);
    }

    private UUID getUserId() {
        UUID userId = TenantContextHolder.getUserId();
        if (userId == null) throw new BusinessRuleException("User ID not found in context");
        return userId;
    }
}
