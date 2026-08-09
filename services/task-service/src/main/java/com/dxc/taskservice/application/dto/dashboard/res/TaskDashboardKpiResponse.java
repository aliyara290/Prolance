package com.dxc.taskservice.application.dto.dashboard.res;

public record TaskDashboardKpiResponse(
        TaskKpiSummary summary,
        TaskKpiCharts charts,
        TaskKpiRecentActivity recentActivity
) {}
