package com.dxc.taskservice.application.dto.assignment.req;

import com.dxc.taskservice.domain.model.valueobject.RoleInTask;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateAssignmentRequest(
    RoleInTask role,

    @PositiveOrZero(message = "Allocation percentage must be zero or positive")
    @Max(value = 100, message = "Allocation percentage cannot exceed 100")
    Float allocationPercentage
) {}
