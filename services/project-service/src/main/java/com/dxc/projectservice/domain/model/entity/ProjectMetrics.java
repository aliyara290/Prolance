package com.dxc.projectservice.domain.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProjectMetrics {
    private UUID id;
    private UUID tenantId;
    private int totalTasks;
    private int completedTasks;
    private int overdueTasks;
    private int velocity;
    private int efficiencyScore;
    private LocalDateTime calculatedAt;
}
