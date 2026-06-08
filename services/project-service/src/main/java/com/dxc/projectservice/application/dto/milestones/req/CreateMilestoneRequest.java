package com.dxc.projectservice.application.dto.milestones.req;

import java.time.LocalDateTime;

public record CreateMilestoneRequest(
    String title,
    String description,
    LocalDateTime startDate,
    LocalDateTime dueDate,
    int sequenceOrder
) {}
