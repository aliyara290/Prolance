package com.dxc.taskservice.application.dto.projectdashboard;

public record ProjectTaskKpiSummary(
        long totalTasks,
        long completedTasks,
        long activeTasks,
        long todoTasks,
        long inProgressTasks,
        long inReviewTasks,
        long cancelledTasks,
        long overdueTasks,
        double completionRate,
        Double averageCompletionTimeHours,
        Double averageTaskAgeHours,
        long highPriorityTasks,
        long urgentPriorityTasks,
        long tasksDueToday,
        long tasksDueThisWeek
) {}
