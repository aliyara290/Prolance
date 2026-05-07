package com.dxc.projectservice.domain.model.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectMetrics {
    private final UUID id;
    private final UUID tenantId;
    private UUID projectId;
    private int totalTasks;
    private int completedTasks;
    private int overdueTasks;
    private int velocity;
    private int efficiencyScore;
    private LocalDateTime calculatedAt;

    public static ProjectMetrics initial(UUID tenantId) {
        return ProjectMetrics.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    public void update(int total, int completed, int overdue, int velocity, int score) {
        this.totalTasks = total;
        this.completedTasks = completed;
        this.overdueTasks = overdue;
        this.velocity = velocity;
        this.efficiencyScore = score;
        this.calculatedAt = LocalDateTime.now();
    }
}
