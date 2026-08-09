package com.dxc.projectservice.application.dto.dashboard.res;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lightweight project summary for the "recent activity" section of the dashboard.
 */
public record RecentProjectDto(
        UUID id,
        String name,
        String prefix,
        String status,
        String priority,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {}
