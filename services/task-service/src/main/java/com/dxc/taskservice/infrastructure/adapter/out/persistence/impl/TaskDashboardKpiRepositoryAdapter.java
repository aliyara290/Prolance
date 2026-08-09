package com.dxc.taskservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.taskservice.application.dto.dashboard.res.*;
import com.dxc.taskservice.application.port.out.TaskDashboardKpiRepository;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;
import com.dxc.taskservice.infrastructure.adapter.out.persistence.jpa.TaskDashboardKpiRepositoryJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Infrastructure adapter implementing {@link TaskDashboardKpiRepository}
 * by delegating to the JPA repository and mapping raw Object[] results to DTOs.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class TaskDashboardKpiRepositoryAdapter implements TaskDashboardKpiRepository {

    private final TaskDashboardKpiRepositoryJpa jpaRepository;

    @Override
    public long countAll(UUID tenantId) {
        return jpaRepository.countAllByTenantId(tenantId);
    }

    @Override
    public long countByStatus(UUID tenantId, TaskStatus status) {
        return jpaRepository.countByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public long countOverdue(UUID tenantId, LocalDateTime now) {
        return jpaRepository.countOverdue(tenantId, now);
    }

    @Override
    public long countDueBetween(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpaRepository.countDueBetween(tenantId, from, to);
    }

    @Override
    public long countCompletedBetween(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpaRepository.countCompletedBetween(tenantId, from, to);
    }

    @Override
    public long countCreatedBetween(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpaRepository.countCreatedBetween(tenantId, from, to);
    }

    @Override
    public Double averageCompletionTimeHours(UUID tenantId) {
        Double avg = jpaRepository.averageCompletionTimeHours(tenantId);
        if (avg == null) {
            return null;
        }
        return Math.round(avg * 100.0) / 100.0;
    }

    @Override
    public List<StatusCountDto> countGroupedByStatus(UUID tenantId) {
        List<Object[]> results = jpaRepository.countGroupedByStatus(tenantId);
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }
        return results.stream()
                .map(row -> new StatusCountDto(
                        (String) row[0],
                        ((Number) row[1]).longValue()))
                .toList();
    }

    @Override
    public List<PriorityCountDto> countGroupedByPriority(UUID tenantId) {
        List<Object[]> results = jpaRepository.countGroupedByPriority(tenantId);
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }
        return results.stream()
                .map(row -> new PriorityCountDto(
                        (String) row[0],
                        ((Number) row[1]).longValue()))
                .toList();
    }

    @Override
    public List<MonthlyCountDto> countCreatedPerMonth(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return mapToMonthlyCountDtos(jpaRepository.countCreatedPerMonth(tenantId, from, to));
    }

    @Override
    public List<MonthlyCountDto> countCompletedPerMonth(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return mapToMonthlyCountDtos(jpaRepository.countCompletedPerMonth(tenantId, from, to));
    }

    @Override
    public List<RecentTaskDto> findRecentlyCreated(UUID tenantId, int limit) {
        return mapToRecentTaskDtos(jpaRepository.findRecentlyCreated(tenantId, limit));
    }

    @Override
    public List<RecentTaskDto> findRecentlyCompleted(UUID tenantId, int limit) {
        return mapToRecentTaskDtos(jpaRepository.findRecentlyCompleted(tenantId, limit));
    }

    // ── Mapping helpers ────────────────────────────────────────────────

    private List<MonthlyCountDto> mapToMonthlyCountDtos(List<Object[]> results) {
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }
        return results.stream()
                .map(row -> new MonthlyCountDto(
                        ((String) row[0]).trim(),
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).longValue()))
                .toList();
    }

    private List<RecentTaskDto> mapToRecentTaskDtos(List<Object[]> results) {
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }
        return results.stream()
                .map(row -> new RecentTaskDto(
                        (UUID) row[0],
                        (UUID) row[1],
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        (String) row[5],
                        (LocalDateTime) row[6],
                        (LocalDateTime) row[7]))
                .toList();
    }
}
