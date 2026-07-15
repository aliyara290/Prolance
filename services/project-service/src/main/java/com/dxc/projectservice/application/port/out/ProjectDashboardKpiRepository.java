package com.dxc.projectservice.application.port.out;

import com.dxc.projectservice.application.dto.dashboard.res.MonthlyCountDto;
import com.dxc.projectservice.application.dto.dashboard.res.RecentProjectDto;
import com.dxc.projectservice.application.dto.dashboard.res.StatusCountDto;
import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Output port for project dashboard KPI data access.
 * All methods are tenant-scoped and designed for aggregate queries.
 */
public interface ProjectDashboardKpiRepository {

    /** Total number of projects for the tenant. */
    long countAll(UUID tenantId);

    /** Count of projects with a specific status. */
    long countByStatus(UUID tenantId, ProjectStatus status);

    /**
     * Count of overdue projects: status is ACTIVE or PLANNED,
     * planned end date has passed, and project is not completed.
     */
    long countOverdue(UUID tenantId, LocalDateTime now);

    /** Count of projects created within the given time range. */
    long countCreatedBetween(UUID tenantId, LocalDateTime from, LocalDateTime to);

    /** Count of projects completed (actualEndDate set) within the given time range. */
    long countCompletedBetween(UUID tenantId, LocalDateTime from, LocalDateTime to);

    /** Projects grouped by status for pie/donut charts. */
    List<StatusCountDto> countGroupedByStatus(UUID tenantId);

    /** Monthly creation counts for the given time range (bar/line chart). */
    List<MonthlyCountDto> countCreatedPerMonth(UUID tenantId, LocalDateTime from, LocalDateTime to);

    /** Monthly completion counts for the given time range (bar/line chart). */
    List<MonthlyCountDto> countCompletedPerMonth(UUID tenantId, LocalDateTime from, LocalDateTime to);

    /** Most recently created projects, limited by count. */
    List<RecentProjectDto> findRecentlyCreated(UUID tenantId, int limit);

    /** Most recently completed projects, limited by count. */
    List<RecentProjectDto> findRecentlyCompleted(UUID tenantId, int limit);
}
