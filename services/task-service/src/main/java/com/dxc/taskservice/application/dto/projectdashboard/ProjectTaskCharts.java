package com.dxc.taskservice.application.dto.projectdashboard;

import com.dxc.taskservice.application.dto.dashboard.res.MonthlyCountDto;
import com.dxc.taskservice.application.dto.dashboard.res.PriorityCountDto;
import com.dxc.taskservice.application.dto.dashboard.res.StatusCountDto;

import java.util.List;

public record ProjectTaskCharts(
        List<StatusCountDto> tasksByStatus,
        List<PriorityCountDto> tasksByPriority,
        List<MonthlyCountDto> tasksCreatedOverTime,
        List<MonthlyCountDto> tasksCompletedOverTime,
        List<CompletionTrendDto> completionTrend,
        List<AssigneeWorkloadDto> assigneeWorkload
) {}
