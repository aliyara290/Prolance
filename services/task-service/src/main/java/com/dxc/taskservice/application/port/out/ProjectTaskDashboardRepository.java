package com.dxc.taskservice.application.port.out;

import com.dxc.taskservice.application.dto.dashboard.res.RecentTaskDto;
import com.dxc.taskservice.application.dto.projectdashboard.AssigneeWorkloadDto;
import com.dxc.taskservice.domain.model.valueobject.TaskPriority;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ProjectTaskDashboardRepository {

    // ── Scalar counts ──────────────────────────────────────────────────

    long countAll(UUID tenantId, UUID projectId,
                  LocalDateTime startDate, LocalDateTime endDate,
                  UUID assigneeId, TaskPriority priority, TaskStatus status);

    long countByStatus(UUID tenantId, UUID projectId, TaskStatus targetStatus,
                       LocalDateTime startDate, LocalDateTime endDate,
                       UUID assigneeId, TaskPriority priority);

    long countOverdue(UUID tenantId, UUID projectId, LocalDateTime now,
                      LocalDateTime startDate, LocalDateTime endDate,
                      UUID assigneeId, TaskPriority priority);

    long countByPriority(UUID tenantId, UUID projectId, TaskPriority targetPriority,
                         LocalDateTime startDate, LocalDateTime endDate,
                         UUID assigneeId, TaskStatus status);

    long countDueBetween(UUID tenantId, UUID projectId,
                         LocalDateTime dueFrom, LocalDateTime dueTo,
                         LocalDateTime startDate, LocalDateTime endDate,
                         UUID assigneeId, TaskPriority priority, TaskStatus status);

    Double averageCompletionTimeHours(UUID tenantId, UUID projectId,
                                      LocalDateTime startDate, LocalDateTime endDate,
                                      UUID assigneeId, TaskPriority priority);

    Double averageTaskAgeHours(UUID tenantId, UUID projectId, LocalDateTime now,
                               LocalDateTime startDate, LocalDateTime endDate,
                               UUID assigneeId, TaskPriority priority, TaskStatus status);

    // ── Chart datasets ─────────────────────────────────────────────────

    List<Object[]> countGroupedByStatus(UUID tenantId, UUID projectId,
                                        LocalDateTime startDate, LocalDateTime endDate,
                                        UUID assigneeId, TaskPriority priority);

    List<Object[]> countGroupedByPriority(UUID tenantId, UUID projectId,
                                          LocalDateTime startDate, LocalDateTime endDate,
                                          UUID assigneeId, TaskStatus status);

    List<Object[]> countCreatedPerMonth(UUID tenantId, UUID projectId,
                                        LocalDateTime chartFrom, LocalDateTime chartTo,
                                        UUID assigneeId, TaskPriority priority, TaskStatus status);

    List<Object[]> countCompletedPerMonth(UUID tenantId, UUID projectId,
                                          LocalDateTime chartFrom, LocalDateTime chartTo,
                                          UUID assigneeId, TaskPriority priority);

    List<AssigneeWorkloadDto> countAssigneeWorkload(UUID tenantId, UUID projectId,
                                                     LocalDateTime startDate, LocalDateTime endDate,
                                                     TaskPriority priority, TaskStatus status);

    // ── Recent activity ────────────────────────────────────────────────

    List<RecentTaskDto> findRecentlyCreated(UUID tenantId, UUID projectId, int limit,
                                            LocalDateTime startDate, LocalDateTime endDate,
                                            UUID assigneeId, TaskPriority priority, TaskStatus status);

    List<RecentTaskDto> findRecentlyCompleted(UUID tenantId, UUID projectId, int limit,
                                              LocalDateTime startDate, LocalDateTime endDate,
                                              UUID assigneeId, TaskPriority priority);
}
