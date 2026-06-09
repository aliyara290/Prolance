package com.dxc.projectservice.application.dto.member.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateMemberAllocationRequest(
    @Min(value = 1, message = "allocationPercentage must be at least 1")
    @Max(value = 100, message = "allocationPercentage cannot exceed 100")
    int allocationPercentage
) {}
