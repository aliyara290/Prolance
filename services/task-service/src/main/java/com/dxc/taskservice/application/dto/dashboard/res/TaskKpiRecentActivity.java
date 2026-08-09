package com.dxc.taskservice.application.dto.dashboard.res;

import java.util.List;

public record TaskKpiRecentActivity(
        List<RecentTaskDto> recentlyCreated,
        List<RecentTaskDto> recentlyCompleted
) {}
