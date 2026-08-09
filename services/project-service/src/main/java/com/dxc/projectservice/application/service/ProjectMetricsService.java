package com.dxc.projectservice.application.service;

import com.dxc.projectservice.application.dto.dashboard.res.*;
import com.dxc.projectservice.application.port.in.ProjectMetricsUseCase;
import com.dxc.projectservice.application.port.out.ProjectDashboardKpiRepository;
import com.dxc.projectservice.application.security.TenantGuard;
import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;
import com.dxc.projectservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectMetricsService implements ProjectMetricsUseCase {

    private final ProjectDashboardKpiRepository kpiRepository;
    private final TenantGuard tenantGuard;

    private static final int RECENT_ITEMS_LIMIT = 5;
    private static final int CHART_MONTHS_RANGE = 12;

    @Override
    public ProjectDashboardKpiResponse getDashboardKpis() {
        UUID tenantId = getTenantIdAndVerify();
        LocalDateTime now = LocalDateTime.now();

        ProjectKpiSummary summary = buildSummary(tenantId, now);
        ProjectKpiCharts charts = buildCharts(tenantId, now);
        ProjectKpiRecentActivity recentActivity = buildRecentActivity(tenantId);

        return new ProjectDashboardKpiResponse(summary, charts, recentActivity);
    }

    private ProjectKpiSummary buildSummary(UUID tenantId, LocalDateTime now) {
        long total = kpiRepository.countAll(tenantId);
        long active = kpiRepository.countByStatus(tenantId, ProjectStatus.ACTIVE);
        long completed = kpiRepository.countByStatus(tenantId, ProjectStatus.COMPLETED);
        long onHold = kpiRepository.countByStatus(tenantId, ProjectStatus.ON_HOLD);
        long cancelled = kpiRepository.countByStatus(tenantId, ProjectStatus.CANCELLED);
        long planned = kpiRepository.countByStatus(tenantId, ProjectStatus.PLANNED);
        long overdue = kpiRepository.countOverdue(tenantId, now);

        // Current month boundaries
        LocalDateTime thisMonthStart = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime thisMonthEnd = now;

        // Previous month boundaries
        LocalDate lastMonthDate = now.toLocalDate().minusMonths(1);
        LocalDateTime lastMonthStart = lastMonthDate.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime lastMonthEnd = lastMonthDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        long createdThisMonth = kpiRepository.countCreatedBetween(tenantId, thisMonthStart, thisMonthEnd);
        long createdLastMonth = kpiRepository.countCreatedBetween(tenantId, lastMonthStart, lastMonthEnd);
        Double createdChangePercent = calculateChangePercent(createdThisMonth, createdLastMonth);

        long completedThisMonth = kpiRepository.countCompletedBetween(tenantId, thisMonthStart, thisMonthEnd);
        long completedLastMonth = kpiRepository.countCompletedBetween(tenantId, lastMonthStart, lastMonthEnd);
        Double completedChangePercent = calculateChangePercent(completedThisMonth, completedLastMonth);

        double completionRate = total > 0 ? Math.round((double) completed / total * 10000.0) / 100.0 : 0.0;

        return new ProjectKpiSummary(
                total, active, completed, onHold, cancelled, planned, overdue,
                createdThisMonth, createdLastMonth, createdChangePercent,
                completedThisMonth, completedLastMonth, completedChangePercent,
                completionRate);
    }

    private ProjectKpiCharts buildCharts(UUID tenantId, LocalDateTime now) {
        LocalDateTime chartsFrom = now.minusMonths(CHART_MONTHS_RANGE)
                .with(TemporalAdjusters.firstDayOfMonth())
                .with(LocalTime.MIN);

        List<StatusCountDto> byStatus = kpiRepository.countGroupedByStatus(tenantId);
        List<MonthlyCountDto> createdPerMonth = kpiRepository.countCreatedPerMonth(tenantId, chartsFrom, now);
        List<MonthlyCountDto> completedPerMonth = kpiRepository.countCompletedPerMonth(tenantId, chartsFrom, now);

        return new ProjectKpiCharts(byStatus, createdPerMonth, completedPerMonth);
    }

    private ProjectKpiRecentActivity buildRecentActivity(UUID tenantId) {
        List<RecentProjectDto> recentlyCreated = kpiRepository.findRecentlyCreated(tenantId, RECENT_ITEMS_LIMIT);
        List<RecentProjectDto> recentlyCompleted = kpiRepository.findRecentlyCompleted(tenantId, RECENT_ITEMS_LIMIT);

        return new ProjectKpiRecentActivity(recentlyCreated, recentlyCompleted);
    }

    /**
     * Calculates the percentage change between two periods.
     * Returns null when the previous period had zero items (avoids division by
     * zero).
     */
    private Double calculateChangePercent(long current, long previous) {
        if (previous == 0) {
            return current > 0 ? null : 0.0;
        }
        return Math.round((double) (current - previous) / previous * 10000.0) / 100.0;
    }

    private UUID getTenantIdAndVerify() {
        String tenantIdStr = TenantContextHolder.getTenantId();
        if (tenantIdStr == null) {
            throw new IllegalArgumentException("Tenant context is missing");
        }
        UUID tenantId = UUID.fromString(tenantIdStr);
        tenantGuard.ensureTenantIsActive(tenantId);
        return tenantId;
    }
}
