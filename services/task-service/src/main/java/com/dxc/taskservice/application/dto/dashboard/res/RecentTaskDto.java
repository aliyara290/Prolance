package com.dxc.taskservice.application.dto.dashboard.res;

import java.time.LocalDateTime;
import java.util.UUID;


public record RecentTaskDto(
        UUID id,
        UUID projectId,
        String title,
        String status,
        String priority,
        String type,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {}
