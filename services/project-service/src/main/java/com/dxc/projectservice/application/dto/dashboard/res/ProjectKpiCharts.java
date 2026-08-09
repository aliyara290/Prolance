package com.dxc.projectservice.application.dto.dashboard.res;

import java.util.List;

/**
 * Chart datasets for the project dashboard.
 * Each field maps directly to a frontend chart component.
 */
public record ProjectKpiCharts(
        List<StatusCountDto> projectsByStatus,
        List<MonthlyCountDto> projectsCreatedPerMonth,
        List<MonthlyCountDto> projectsCompletedPerMonth
) {}
