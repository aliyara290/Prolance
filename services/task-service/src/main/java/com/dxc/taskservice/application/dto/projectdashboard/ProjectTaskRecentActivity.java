package com.dxc.taskservice.application.dto.projectdashboard;

import com.dxc.taskservice.application.dto.dashboard.res.RecentTaskDto;

import java.util.List;

public record ProjectTaskRecentActivity(
        List<RecentTaskDto> recentlyCreated,
        List<RecentTaskDto> recentlyCompleted
) {}
