package com.dxc.projectservice.application.dto.milestones.req;

import com.dxc.projectservice.domain.model.valueobject.MilestoneStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateMilestoneRequest(
    @NotBlank(message = "title is required")
    @Size(min = 3, max = 100, message = "title must be between 3 and 100 characters")
    String title,

    @Size(max = 500, message = "description cannot exceed 500 characters")
    String description,

    @NotNull(message = "startDate is required")
    LocalDateTime startDate,

    @NotNull(message = "dueDate is required")
    LocalDateTime dueDate,

    @PositiveOrZero(message = "sequenceOrder must be zero or positive")
    int sequenceOrder,

    Float progressPercentage,

    MilestoneStatus status
) {}
