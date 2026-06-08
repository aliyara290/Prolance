package com.dxc.projectservice.application.dto.milestones.res;

import com.dxc.projectservice.domain.model.valueobject.MilestoneStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record MilestoneResponse(
    UUID id,
    UUID projectId,
    String title,
    String description,
    MilestoneStatus status,
    LocalDateTime startDate,
    LocalDateTime dueDate,
    LocalDateTime completedAt,
    int sequenceOrder,
    float progressPercentage,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
