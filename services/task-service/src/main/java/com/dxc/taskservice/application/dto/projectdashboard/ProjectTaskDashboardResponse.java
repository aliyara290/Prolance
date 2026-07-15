package com.dxc.taskservice.application.dto.projectdashboard;

public record ProjectTaskDashboardResponse(
        ProjectTaskKpiSummary summary,
        ProjectTaskCharts charts,
        ProjectTaskRecentActivity recentActivity
) {}
