package com.dxc.taskservice.application.port.out;

import com.dxc.taskservice.application.dto.dashboard.res.MonthlyCountDto;
import com.dxc.taskservice.application.dto.dashboard.res.PriorityCountDto;
import com.dxc.taskservice.application.dto.dashboard.res.RecentTaskDto;
import com.dxc.taskservice.application.dto.dashboard.res.StatusCountDto;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Output port for task dashboard KPI data access.
 * All methods are tenant-scoped and designed for aggregate queries.
 */
public interface TaskDashboardKpiRepository {

    /** Total number of tasks for the tenant. */
    long countAll(UUID tenantId);

    /** Count of tasks with a specific status. */
    long countByStatus(UUID tenantId, TaskStatus status);

    /**
     * Count of overdue tasks: dueDate has passed
     * and status is not DONE or CANCELLED.
     */
    long countOverdue(UUID tenantId, LocalDateTime now);

    /** Count of tasks due on a specific date (day boundaries). */
    long countDueBetween(UUID tenantId, LocalDateTime from, LocalDateTime to);

    /** Count of tasks completed within the given time range. */
    long countCompletedBetween(UUID tenantId, LocalDateTime from, LocalDateTime to);

    /** Count of tasks created within the given time range. */
    long countCreatedBetween(UUID tenantId, LocalDateTime from, LocalDateTime to);

    /**
     * Average completion time in hours for tasks completed within the given range.
     * Returns null if no completed tasks exist.
     */
    Double averageCompletionTimeHours(UUID tenantId);

    /** Tasks grouped by status for pie/donut charts. */
    List<StatusCountDto> countGroupedByStatus(UUID tenantId);

    /** Tasks grouped by priority for pie/bar charts. */
    List<PriorityCountDto> countGroupedByPriority(UUID tenantId);

    /** Monthly creation counts for the given time range (bar/line chart). */
    List<MonthlyCountDto> countCreatedPerMonth(UUID tenantId, LocalDateTime from, LocalDateTime to);

    /** Monthly completion counts for the given time range (bar/line chart). */
    List<MonthlyCountDto> countCompletedPerMonth(UUID tenantId, LocalDateTime from, LocalDateTime to);

    /** Most recently created tasks, limited by count. */
    List<RecentTaskDto> findRecentlyCreated(UUID tenantId, int limit);

    /** Most recently completed tasks, limited by count. */
    List<RecentTaskDto> findRecentlyCompleted(UUID tenantId, int limit);
}
