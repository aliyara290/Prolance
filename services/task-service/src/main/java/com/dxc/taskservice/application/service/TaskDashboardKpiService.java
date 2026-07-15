package com.dxc.taskservice.application.service;

import com.dxc.taskservice.application.dto.dashboard.res.*;
import com.dxc.taskservice.application.port.in.TaskDashboardKpiUseCase;
import com.dxc.taskservice.application.port.out.TaskDashboardKpiRepository;
import com.dxc.taskservice.application.security.TenantGuard;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;
import com.dxc.taskservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

/**
 * Application service that composes KPI data from the dashboard repository
 * and assembles the final task dashboard response.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskDashboardKpiService implements TaskDashboardKpiUseCase {

    private final TaskDashboardKpiRepository kpiRepository;
    private final TenantGuard tenantGuard;

    private static final int RECENT_ITEMS_LIMIT = 5;
    private static final int CHART_MONTHS_RANGE = 12;

    @Override
    public TaskDashboardKpiResponse getDashboardKpis() {
        UUID tenantId = getTenantIdAndVerify();
        LocalDateTime now = LocalDateTime.now();

        TaskKpiSummary summary = buildSummary(tenantId, now);
        TaskKpiCharts charts = buildCharts(tenantId, now);
        TaskKpiRecentActivity recentActivity = buildRecentActivity(tenantId);

        return new TaskDashboardKpiResponse(summary, charts, recentActivity);
    }

    private TaskKpiSummary buildSummary(UUID tenantId, LocalDateTime now) {
        long total = kpiRepository.countAll(tenantId);
        long todo = kpiRepository.countByStatus(tenantId, TaskStatus.TODO);
        long inProgress = kpiRepository.countByStatus(tenantId, TaskStatus.IN_PROGRESS);
        long inReview = kpiRepository.countByStatus(tenantId, TaskStatus.IN_REVIEW);
        long completed = kpiRepository.countByStatus(tenantId, TaskStatus.DONE);
        long cancelled = kpiRepository.countByStatus(tenantId, TaskStatus.CANCELLED);
        long overdue = kpiRepository.countOverdue(tenantId, now);

        // Today boundaries
        LocalDate today = now.toLocalDate();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);

        // Yesterday boundaries
        LocalDate yesterday = today.minusDays(1);
        LocalDateTime yesterdayStart = yesterday.atStartOfDay();
        LocalDateTime yesterdayEnd = yesterday.atTime(LocalTime.MAX);

        // This week boundaries (Monday to Sunday)
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime thisWeekStart = weekStart.atStartOfDay();
        LocalDateTime thisWeekEnd = now;

        // Last week boundaries
        LocalDate lastWeekStart = weekStart.minusWeeks(1);
        LocalDate lastWeekEnd = weekStart.minusDays(1);
        LocalDateTime lastWeekStartDt = lastWeekStart.atStartOfDay();
        LocalDateTime lastWeekEndDt = lastWeekEnd.atTime(LocalTime.MAX);

        // This month boundaries
        LocalDateTime thisMonthStart = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime thisMonthEnd = now;

        // Last month boundaries
        LocalDate lastMonthDate = now.toLocalDate().minusMonths(1);
        LocalDateTime lastMonthStart = lastMonthDate.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime lastMonthEnd = lastMonthDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        // Due counts
        long dueToday = kpiRepository.countDueBetween(tenantId, todayStart, todayEnd);
        long dueThisWeek = kpiRepository.countDueBetween(tenantId, thisWeekStart, thisWeekEnd);

        // Completed counts
        long completedToday = kpiRepository.countCompletedBetween(tenantId, todayStart, todayEnd);
        long completedYesterday = kpiRepository.countCompletedBetween(tenantId, yesterdayStart, yesterdayEnd);
        Double completedTodayChangePercent = calculateChangePercent(completedToday, completedYesterday);

        long completedThisWeek = kpiRepository.countCompletedBetween(tenantId, thisWeekStart, thisWeekEnd);
        long completedLastWeek = kpiRepository.countCompletedBetween(tenantId, lastWeekStartDt, lastWeekEndDt);
        Double completedWeekChangePercent = calculateChangePercent(completedThisWeek, completedLastWeek);

        // Created counts
        long createdThisMonth = kpiRepository.countCreatedBetween(tenantId, thisMonthStart, thisMonthEnd);
        long createdLastMonth = kpiRepository.countCreatedBetween(tenantId, lastMonthStart, lastMonthEnd);
        Double createdMonthChangePercent = calculateChangePercent(createdThisMonth, createdLastMonth);

        // Rates
        double completionRate = total > 0 ? Math.round((double) completed / total * 10000.0) / 100.0 : 0.0;
        Double avgCompletionTime = kpiRepository.averageCompletionTimeHours(tenantId);

        return new TaskKpiSummary(
                total, todo, inProgress, inReview, completed, cancelled, overdue,
                dueToday, dueThisWeek,
                completedToday, completedYesterday, completedTodayChangePercent,
                completedThisWeek, completedLastWeek, completedWeekChangePercent,
                createdThisMonth, createdLastMonth, createdMonthChangePercent,
                completionRate, avgCompletionTime);
    }

    private TaskKpiCharts buildCharts(UUID tenantId, LocalDateTime now) {
        LocalDateTime chartsFrom = now.minusMonths(CHART_MONTHS_RANGE)
                .with(TemporalAdjusters.firstDayOfMonth())
                .with(LocalTime.MIN);

        List<StatusCountDto> byStatus = kpiRepository.countGroupedByStatus(tenantId);
        List<PriorityCountDto> byPriority = kpiRepository.countGroupedByPriority(tenantId);
        List<MonthlyCountDto> createdPerMonth = kpiRepository.countCreatedPerMonth(tenantId, chartsFrom, now);
        List<MonthlyCountDto> completedPerMonth = kpiRepository.countCompletedPerMonth(tenantId, chartsFrom, now);

        return new TaskKpiCharts(byStatus, byPriority, createdPerMonth, completedPerMonth);
    }

    private TaskKpiRecentActivity buildRecentActivity(UUID tenantId) {
        List<RecentTaskDto> recentlyCreated = kpiRepository.findRecentlyCreated(tenantId, RECENT_ITEMS_LIMIT);
        List<RecentTaskDto> recentlyCompleted = kpiRepository.findRecentlyCompleted(tenantId, RECENT_ITEMS_LIMIT);

        return new TaskKpiRecentActivity(recentlyCreated, recentlyCompleted);
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
