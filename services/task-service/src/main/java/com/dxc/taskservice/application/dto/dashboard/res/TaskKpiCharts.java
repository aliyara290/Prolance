package com.dxc.taskservice.application.dto.dashboard.res;

import java.util.List;


public record TaskKpiCharts(
        List<StatusCountDto> tasksByStatus,
        List<PriorityCountDto> tasksByPriority,
        List<MonthlyCountDto> tasksCreatedPerMonth,
        List<MonthlyCountDto> tasksCompletedPerMonth
) {}
