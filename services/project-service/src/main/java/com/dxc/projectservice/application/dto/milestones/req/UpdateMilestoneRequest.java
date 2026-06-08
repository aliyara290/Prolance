package com.dxc.projectservice.application.dto.milestones.req;

import java.time.LocalDateTime;

public record UpdateMilestoneRequest(
    String title,
    String description,
    LocalDateTime startDate,
    LocalDateTime dueDate
) {}
