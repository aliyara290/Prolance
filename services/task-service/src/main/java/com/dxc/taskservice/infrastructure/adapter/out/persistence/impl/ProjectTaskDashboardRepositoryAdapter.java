package com.dxc.taskservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.taskservice.application.dto.dashboard.res.RecentTaskDto;
import com.dxc.taskservice.application.dto.projectdashboard.AssigneeWorkloadDto;
import com.dxc.taskservice.application.port.out.ProjectTaskDashboardRepository;
import com.dxc.taskservice.domain.model.valueobject.TaskPriority;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;
import com.dxc.taskservice.infrastructure.adapter.out.persistence.jpa.ProjectTaskDashboardRepositoryJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProjectTaskDashboardRepositoryAdapter implements ProjectTaskDashboardRepository {

    private final ProjectTaskDashboardRepositoryJpa jpa;

    // ── Scalar counts ──────────────────────────────────────────────────

    @Override
    public long countAll(UUID tenantId, UUID projectId,
                         LocalDateTime startDate, LocalDateTime endDate,
                         UUID assigneeId, TaskPriority priority, TaskStatus status) {
        return jpa.countAll(tenantId, projectId, startDate, endDate, assigneeId,
                enumName(priority), enumName(status));
    }

    @Override
    public long countByStatus(UUID tenantId, UUID projectId, TaskStatus targetStatus,
                              LocalDateTime startDate, LocalDateTime endDate,
                              UUID assigneeId, TaskPriority priority) {
        return jpa.countByStatus(tenantId, projectId, targetStatus.name(),
                startDate, endDate, assigneeId, enumName(priority));
    }

    @Override
    public long countOverdue(UUID tenantId, UUID projectId, LocalDateTime now,
                             LocalDateTime startDate, LocalDateTime endDate,
                             UUID assigneeId, TaskPriority priority) {
        return jpa.countOverdue(tenantId, projectId, now, startDate, endDate,
                assigneeId, enumName(priority));
    }

    @Override
    public long countByPriority(UUID tenantId, UUID projectId, TaskPriority targetPriority,
                                LocalDateTime startDate, LocalDateTime endDate,
                                UUID assigneeId, TaskStatus status) {
        return jpa.countByPriority(tenantId, projectId, targetPriority.name(),
                startDate, endDate, assigneeId, enumName(status));
    }

    @Override
    public long countDueBetween(UUID tenantId, UUID projectId,
                                LocalDateTime dueFrom, LocalDateTime dueTo,
                                LocalDateTime startDate, LocalDateTime endDate,
                                UUID assigneeId, TaskPriority priority, TaskStatus status) {
        return jpa.countDueBetween(tenantId, projectId, dueFrom, dueTo,
                startDate, endDate, assigneeId, enumName(priority), enumName(status));
    }

    @Override
    public Double averageCompletionTimeHours(UUID tenantId, UUID projectId,
                                              LocalDateTime startDate, LocalDateTime endDate,
                                              UUID assigneeId, TaskPriority priority) {
        return jpa.averageCompletionTimeHours(tenantId, projectId, startDate, endDate,
                assigneeId, enumName(priority));
    }

    @Override
    public Double averageTaskAgeHours(UUID tenantId, UUID projectId, LocalDateTime now,
                                       LocalDateTime startDate, LocalDateTime endDate,
                                       UUID assigneeId, TaskPriority priority, TaskStatus status) {
        return jpa.averageTaskAgeHours(tenantId, projectId, now, startDate, endDate,
                assigneeId, enumName(priority), enumName(status));
    }

    // ── Chart datasets ─────────────────────────────────────────────────

    @Override
    public List<Object[]> countGroupedByStatus(UUID tenantId, UUID projectId,
                                                LocalDateTime startDate, LocalDateTime endDate,
                                                UUID assigneeId, TaskPriority priority) {
        return jpa.countGroupedByStatus(tenantId, projectId, startDate, endDate,
                assigneeId, enumName(priority));
    }

    @Override
    public List<Object[]> countGroupedByPriority(UUID tenantId, UUID projectId,
                                                  LocalDateTime startDate, LocalDateTime endDate,
                                                  UUID assigneeId, TaskStatus status) {
        return jpa.countGroupedByPriority(tenantId, projectId, startDate, endDate,
                assigneeId, enumName(status));
    }

    @Override
    public List<Object[]> countCreatedPerMonth(UUID tenantId, UUID projectId,
                                                LocalDateTime chartFrom, LocalDateTime chartTo,
                                                UUID assigneeId, TaskPriority priority, TaskStatus status) {
        return jpa.countCreatedPerMonth(tenantId, projectId, chartFrom, chartTo,
                assigneeId, enumName(priority), enumName(status));
    }

    @Override
    public List<Object[]> countCompletedPerMonth(UUID tenantId, UUID projectId,
                                                  LocalDateTime chartFrom, LocalDateTime chartTo,
                                                  UUID assigneeId, TaskPriority priority) {
        return jpa.countCompletedPerMonth(tenantId, projectId, chartFrom, chartTo,
                assigneeId, enumName(priority));
    }

    @Override
    public List<AssigneeWorkloadDto> countAssigneeWorkload(UUID tenantId, UUID projectId,
                                                            LocalDateTime startDate, LocalDateTime endDate,
                                                            TaskPriority priority, TaskStatus status) {
        List<Object[]> results = jpa.countAssigneeWorkload(tenantId, projectId,
                startDate, endDate, enumName(priority), enumName(status));
        if (results == null || results.isEmpty()) return Collections.emptyList();
        return results.stream()
                .map(row -> new AssigneeWorkloadDto(
                        (UUID) row[0],
                        ((Number) row[1]).longValue()))
                .toList();
    }

    // ── Recent activity ────────────────────────────────────────────────

    @Override
    public List<RecentTaskDto> findRecentlyCreated(UUID tenantId, UUID projectId, int limit,
                                                    LocalDateTime startDate, LocalDateTime endDate,
                                                    UUID assigneeId, TaskPriority priority, TaskStatus status) {
        return mapToRecentTaskDtos(jpa.findRecentlyCreated(tenantId, projectId, limit,
                startDate, endDate, assigneeId, enumName(priority), enumName(status)));
    }

    @Override
    public List<RecentTaskDto> findRecentlyCompleted(UUID tenantId, UUID projectId, int limit,
                                                      LocalDateTime startDate, LocalDateTime endDate,
                                                      UUID assigneeId, TaskPriority priority) {
        return mapToRecentTaskDtos(jpa.findRecentlyCompleted(tenantId, projectId, limit,
                startDate, endDate, assigneeId, enumName(priority)));
    }

    // ── Helpers ────────────────────────────────────────────────────────

    private String enumName(Enum<?> e) {
        return e != null ? e.name() : null;
    }

    private List<RecentTaskDto> mapToRecentTaskDtos(List<Object[]> results) {
        if (results == null || results.isEmpty()) return Collections.emptyList();
        return results.stream()
                .map(row -> new RecentTaskDto(
                        (UUID) row[0],
                        (UUID) row[1],
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        (String) row[5],
                        toLocalDateTime(row[6]),
                        toLocalDateTime(row[7])))
                .toList();
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDateTime ldt) return ldt;
        if (value instanceof Timestamp ts) return ts.toLocalDateTime();
        return null;
    }
}
