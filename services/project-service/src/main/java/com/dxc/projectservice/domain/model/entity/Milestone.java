package com.dxc.projectservice.domain.model.entity;

import com.dxc.projectservice.domain.model.valueobject.MilestoneStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class Milestone {
    private UUID id;
    private UUID tenantId;
    private String title;
    private String description;
    private MilestoneStatus status;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private int sequenceOrder;
    private float progressPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
