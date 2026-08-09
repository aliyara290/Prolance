package com.dxc.projectservice.application.dto.dashboard.res;

/**
 * Scalar KPI values for the project dashboard summary cards.
 * Includes absolute counts, period-over-period changes, and computed rates.
 */
public record ProjectKpiSummary(
        long totalProjects,
        long activeProjects,
        long completedProjects,
        long onHoldProjects,
        long cancelledProjects,
        long plannedProjects,
        long overdueProjects,
        long projectsCreatedThisMonth,
        long projectsCreatedLastMonth,
        Double projectsCreatedMonthChangePercent,
        long projectsCompletedThisMonth,
        long projectsCompletedLastMonth,
        Double projectsCompletedMonthChangePercent,
        double completionRate
) {}
