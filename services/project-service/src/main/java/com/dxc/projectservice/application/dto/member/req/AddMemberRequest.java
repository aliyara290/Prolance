package com.dxc.projectservice.application.dto.member.req;

import com.dxc.projectservice.domain.model.valueobject.MemberRole;
import com.dxc.projectservice.domain.model.valueobject.MemberStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddMemberRequest(
    @NotNull(message = "userId is required")
    UUID userId,
    
    @NotNull(message = "role is required")
    MemberRole role,
    
    @Min(value = 1, message = "allocationPercentage must be at least 1")
    @Max(value = 100, message = "allocationPercentage cannot exceed 100")
    int allocationPercentage,
    
    MemberStatus status
) {}
