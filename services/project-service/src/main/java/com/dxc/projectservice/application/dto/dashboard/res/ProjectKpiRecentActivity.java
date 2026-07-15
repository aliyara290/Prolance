package com.dxc.projectservice.application.dto.dashboard.res;

import java.util.List;

/**
 * Recent project activity for the dashboard feed.
 */
public record ProjectKpiRecentActivity(
        List<RecentProjectDto> recentlyCreated,
        List<RecentProjectDto> recentlyCompleted
) {}
