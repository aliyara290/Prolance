package com.dxc.projectservice.application.dto.milestones.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateMilestoneProgressRequest(
    @Min(value = 0, message = "progressPercentage cannot be negative")
    @Max(value = 100, message = "progressPercentage cannot exceed 100")
    float progressPercentage
) {}
