package com.dxc.projectservice.application.dto.metrics.res;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectMetricsResponse(
    UUID id,
    UUID projectId,
    int totalTasks,
    int completedTasks,
    int overdueTasks,
    int velocity,
    int efficiencyScore,
    LocalDateTime calculatedAt
) {}
