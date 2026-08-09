package com.dxc.projectservice.application.dto.dashboard.res;

/**
 * Top-level response for the project dashboard KPI endpoint.
 * Wraps summary metrics, chart datasets, and recent activity into a single response.
 */
public record ProjectDashboardKpiResponse(
        ProjectKpiSummary summary,
        ProjectKpiCharts charts,
        ProjectKpiRecentActivity recentActivity
) {}
