package com.dxc.projectservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.projectservice.domain.model.valueobject.MilestoneStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "milestones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE milestones SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class MilestoneEntity extends BaseAuditingEntity {
    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "title", nullable = false)
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private MilestoneStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    private LocalDateTime dueDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder;

    @Column(name = "progress_percentage", nullable = false)
    private float progressPercentage;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
