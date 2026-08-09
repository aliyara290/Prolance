package com.dxc.projectservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.projectservice.application.dto.dashboard.res.MonthlyCountDto;
import com.dxc.projectservice.application.dto.dashboard.res.RecentProjectDto;
import com.dxc.projectservice.application.dto.dashboard.res.StatusCountDto;
import com.dxc.projectservice.application.port.out.ProjectDashboardKpiRepository;
import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.jpa.ProjectDashboardKpiRepositoryJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Infrastructure adapter implementing {@link ProjectDashboardKpiRepository}
 * by delegating to the JPA repository and mapping raw Object[] results to DTOs.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ProjectDashboardKpiRepositoryAdapter implements ProjectDashboardKpiRepository {

    private final ProjectDashboardKpiRepositoryJpa jpaRepository;

    @Override
    public long countAll(UUID tenantId) {
        return jpaRepository.countAllByTenantId(tenantId);
    }

    @Override
    public long countByStatus(UUID tenantId, ProjectStatus status) {
        return jpaRepository.countByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public long countOverdue(UUID tenantId, LocalDateTime now) {
        return jpaRepository.countOverdue(tenantId, now);
    }

    @Override
    public long countCreatedBetween(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpaRepository.countCreatedBetween(tenantId, from, to);
    }

    @Override
    public long countCompletedBetween(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpaRepository.countCompletedBetween(tenantId, from, to);
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
                        ((Number) row[1]).longValue()
                ))
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
    public List<RecentProjectDto> findRecentlyCreated(UUID tenantId, int limit) {
        return mapToRecentProjectDtos(jpaRepository.findRecentlyCreated(tenantId, limit));
    }

    @Override
    public List<RecentProjectDto> findRecentlyCompleted(UUID tenantId, int limit) {
        return mapToRecentProjectDtos(jpaRepository.findRecentlyCompleted(tenantId, limit));
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
                        ((Number) row[2]).longValue()
                ))
                .toList();
    }

    private List<RecentProjectDto> mapToRecentProjectDtos(List<Object[]> results) {
        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }
        return results.stream()
                .map(row -> new RecentProjectDto(
                        (UUID) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        (LocalDateTime) row[5],
                        (LocalDateTime) row[6]
                ))
                .toList();
    }
}
