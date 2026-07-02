package com.dxc.taskservice.application.dto.assignment.res;

import com.dxc.taskservice.domain.model.valueobject.RoleInTask;
import com.dxc.taskservice.domain.model.valueobject.TaskAssignmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskAssignmentResponse(
    UUID id,
    UUID taskId,
    UUID userId,
    RoleInTask role,
    float allocationPercentage,
    TaskAssignmentStatus status,
    LocalDateTime assignedAt,
    LocalDateTime unassignedAt
) {}
