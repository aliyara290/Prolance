package com.dxc.taskservice.application.dto.dashboard.res;


public record TaskKpiSummary(
        long totalTasks,
        long todoTasks,
        long inProgressTasks,
        long inReviewTasks,
        long completedTasks,
        long cancelledTasks,
        long overdueTasks,
        long tasksDueToday,
        long tasksDueThisWeek,
        long tasksCompletedToday,
        long tasksCompletedYesterday,
        Double tasksCompletedTodayChangePercent,
        long tasksCompletedThisWeek,
        long tasksCompletedLastWeek,
        Double tasksCompletedThisWeekChangePercent,
        long tasksCreatedThisMonth,
        long tasksCreatedLastMonth,
        Double tasksCreatedMonthChangePercent,
        double completionRate,
        Double averageCompletionTimeHours
) {}
