package com.dxc.taskservice.application.service;

import com.dxc.taskservice.application.dto.dashboard.res.*;
import com.dxc.taskservice.application.dto.projectdashboard.*;
import com.dxc.taskservice.application.port.in.ProjectTaskDashboardUseCase;
import com.dxc.taskservice.application.port.out.ProjectTaskDashboardRepository;
import com.dxc.taskservice.application.security.TenantGuard;
import com.dxc.taskservice.domain.model.valueobject.TaskPriority;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;
import com.dxc.taskservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectTaskDashboardService implements ProjectTaskDashboardUseCase {

    private final ProjectTaskDashboardRepository repository;
    private final TenantGuard tenantGuard;

    private static final int RECENT_ITEMS_LIMIT = 5;
    private static final int CHART_MONTHS_RANGE = 12;

    @Override
    public ProjectTaskDashboardResponse getProjectDashboard(UUID projectId,
                                                             ProjectTaskDashboardFilterRequest filter) {
        UUID tenantId = getTenantIdAndVerify();
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startDate = filter.startDate() != null ? filter.startDate().atStartOfDay() : null;
        LocalDateTime endDate = filter.endDate() != null ? filter.endDate().atTime(LocalTime.MAX) : null;
        UUID assigneeId = filter.assigneeId();
        TaskPriority priority = filter.priority();
        TaskStatus status = filter.status();

        ProjectTaskKpiSummary summary = buildSummary(tenantId, projectId, now,
                startDate, endDate, assigneeId, priority, status);
        ProjectTaskCharts charts = buildCharts(tenantId, projectId, now,
                startDate, endDate, assigneeId, priority, status);
        ProjectTaskRecentActivity recentActivity = buildRecentActivity(tenantId, projectId,
                startDate, endDate, assigneeId, priority, status);

        return new ProjectTaskDashboardResponse(summary, charts, recentActivity);
    }

    private ProjectTaskKpiSummary buildSummary(UUID tenantId, UUID projectId, LocalDateTime now,
                                                LocalDateTime startDate, LocalDateTime endDate,
                                                UUID assigneeId, TaskPriority priority, TaskStatus status) {
        long total = repository.countAll(tenantId, projectId, startDate, endDate, assigneeId, priority, status);
        long completed = repository.countByStatus(tenantId, projectId, TaskStatus.DONE,
                startDate, endDate, assigneeId, priority);
        long todo = repository.countByStatus(tenantId, projectId, TaskStatus.TODO,
                startDate, endDate, assigneeId, priority);
        long inProgress = repository.countByStatus(tenantId, projectId, TaskStatus.IN_PROGRESS,
                startDate, endDate, assigneeId, priority);
        long inReview = repository.countByStatus(tenantId, projectId, TaskStatus.IN_REVIEW,
                startDate, endDate, assigneeId, priority);
        long cancelled = repository.countByStatus(tenantId, projectId, TaskStatus.CANCELLED,
                startDate, endDate, assigneeId, priority);
        long active = inProgress + inReview;
        long overdue = repository.countOverdue(tenantId, projectId, now,
                startDate, endDate, assigneeId, priority);

        double completionRate = total > 0
                ? Math.round((double) completed / total * 10000.0) / 100.0
                : 0.0;

        Double avgCompletionTime = repository.averageCompletionTimeHours(tenantId, projectId,
                startDate, endDate, assigneeId, priority);
        if (avgCompletionTime != null) {
            avgCompletionTime = Math.round(avgCompletionTime * 100.0) / 100.0;
        }

        Double avgTaskAge = repository.averageTaskAgeHours(tenantId, projectId, now,
                startDate, endDate, assigneeId, priority, status);
        if (avgTaskAge != null) {
            avgTaskAge = Math.round(avgTaskAge * 100.0) / 100.0;
        }

        long highPriority = repository.countByPriority(tenantId, projectId, TaskPriority.HIGH,
                startDate, endDate, assigneeId, status);
        long urgentPriority = repository.countByPriority(tenantId, projectId, TaskPriority.URGENT,
                startDate, endDate, assigneeId, status);

        LocalDate today = now.toLocalDate();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);
        long tasksDueToday = repository.countDueBetween(tenantId, projectId, todayStart, todayEnd,
                startDate, endDate, assigneeId, priority, status);

        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        long tasksDueThisWeek = repository.countDueBetween(tenantId, projectId,
                weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX),
                startDate, endDate, assigneeId, priority, status);

        return new ProjectTaskKpiSummary(
                total, completed, active, todo, inProgress, inReview, cancelled, overdue,
                completionRate, avgCompletionTime, avgTaskAge,
                highPriority, urgentPriority, tasksDueToday, tasksDueThisWeek);
    }

    private ProjectTaskCharts buildCharts(UUID tenantId, UUID projectId, LocalDateTime now,
                                           LocalDateTime startDate, LocalDateTime endDate,
                                           UUID assigneeId, TaskPriority priority, TaskStatus status) {
        LocalDateTime chartFrom = now.minusMonths(CHART_MONTHS_RANGE)
                .with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime chartTo = now;

        List<StatusCountDto> byStatus = mapStatusCounts(
                repository.countGroupedByStatus(tenantId, projectId, startDate, endDate, assigneeId, priority));
        List<PriorityCountDto> byPriority = mapPriorityCounts(
                repository.countGroupedByPriority(tenantId, projectId, startDate, endDate, assigneeId, status));

        List<MonthlyCountDto> createdOverTime = mapMonthlyCountDtos(
                repository.countCreatedPerMonth(tenantId, projectId, chartFrom, chartTo, assigneeId, priority, status));
        List<MonthlyCountDto> completedOverTime = mapMonthlyCountDtos(
                repository.countCompletedPerMonth(tenantId, projectId, chartFrom, chartTo, assigneeId, priority));

        List<CompletionTrendDto> completionTrend = buildCompletionTrend(createdOverTime, completedOverTime);

        List<AssigneeWorkloadDto> workload = repository.countAssigneeWorkload(tenantId, projectId,
                startDate, endDate, priority, status);

        return new ProjectTaskCharts(byStatus, byPriority, createdOverTime, completedOverTime,
                completionTrend, workload);
    }

    private ProjectTaskRecentActivity buildRecentActivity(UUID tenantId, UUID projectId,
                                                           LocalDateTime startDate, LocalDateTime endDate,
                                                           UUID assigneeId, TaskPriority priority, TaskStatus status) {
        List<RecentTaskDto> created = repository.findRecentlyCreated(tenantId, projectId, RECENT_ITEMS_LIMIT,
                startDate, endDate, assigneeId, priority, status);
        List<RecentTaskDto> completed = repository.findRecentlyCompleted(tenantId, projectId, RECENT_ITEMS_LIMIT,
                startDate, endDate, assigneeId, priority);

        return new ProjectTaskRecentActivity(created, completed);
    }

    private List<CompletionTrendDto> buildCompletionTrend(List<MonthlyCountDto> created,
                                                          List<MonthlyCountDto> completed) {
        Map<String, MonthlyCountDto> createdMap = created.stream()
                .collect(Collectors.toMap(m -> m.month().trim() + "-" + m.year(), Function.identity()));
        Map<String, MonthlyCountDto> completedMap = completed.stream()
                .collect(Collectors.toMap(m -> m.month().trim() + "-" + m.year(), Function.identity()));

        Set<String> allKeys = new TreeSet<>();
        allKeys.addAll(createdMap.keySet());
        allKeys.addAll(completedMap.keySet());

        return allKeys.stream()
                .map(key -> {
                    MonthlyCountDto c = createdMap.get(key);
                    MonthlyCountDto d = completedMap.get(key);
                    String month = c != null ? c.month() : d.month();
                    int year = c != null ? c.year() : d.year();
                    long createdCount = c != null ? c.count() : 0;
                    long completedCount = d != null ? d.count() : 0;
                    return new CompletionTrendDto(month, year, createdCount, completedCount);
                })
                .toList();
    }

    // ── Mapping helpers ────────────────────────────────────────────────

    private List<StatusCountDto> mapStatusCounts(List<Object[]> results) {
        if (results == null || results.isEmpty()) return Collections.emptyList();
        return results.stream()
                .map(row -> new StatusCountDto((String) row[0], ((Number) row[1]).longValue()))
                .toList();
    }

    private List<PriorityCountDto> mapPriorityCounts(List<Object[]> results) {
        if (results == null || results.isEmpty()) return Collections.emptyList();
        return results.stream()
                .map(row -> new PriorityCountDto((String) row[0], ((Number) row[1]).longValue()))
                .toList();
    }

    private List<MonthlyCountDto> mapMonthlyCountDtos(List<Object[]> results) {
        if (results == null || results.isEmpty()) return Collections.emptyList();
        return results.stream()
                .map(row -> new MonthlyCountDto(
                        ((String) row[0]).trim(),
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).longValue()))
                .toList();
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
