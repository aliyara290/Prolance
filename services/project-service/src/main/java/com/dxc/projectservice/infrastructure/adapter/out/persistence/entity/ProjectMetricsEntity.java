package com.dxc.projectservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_metrics")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMetricsEntity {
    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "total_tasks", nullable = false)
    private int totalTasks;

    @Column(name = "completed_tasks", nullable = false)
    private int completedTasks;

    @Column(name = "overdue_tasks")
    private int overdueTasks;

    private int velocity;

    @Column(name = "efficiency_score")
    private int efficiencyScore;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;
}
