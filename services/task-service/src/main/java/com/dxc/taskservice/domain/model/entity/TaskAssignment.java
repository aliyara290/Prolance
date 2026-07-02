package com.dxc.taskservice.domain.model.entity;

import com.dxc.taskservice.domain.exception.ValidationException;
import com.dxc.taskservice.domain.model.valueobject.RoleInTask;
import com.dxc.taskservice.domain.model.valueobject.TaskAssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TaskAssignment {

    private final UUID id;
    private final UUID tenantId;
    private final UUID taskId;
    private final UUID userId;

    private RoleInTask role;
    private float allocationPercentage;
    private TaskAssignmentStatus status;

    private final LocalDateTime assignedAt;
    private LocalDateTime unassignedAt;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskAssignment create(
            UUID tenantId,
            UUID taskId,
            UUID userId,
            RoleInTask role,
            float allocationPercentage
    ) {
        validateRequired(tenantId, "Tenant ID is required");
        validateRequired(taskId, "Task ID is required");
        validateRequired(userId, "User ID is required");
        validateRequired(role, "Role is required");
        validateAllocation(allocationPercentage);

        LocalDateTime now = LocalDateTime.now();

        return TaskAssignment.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .taskId(taskId)
                .userId(userId)
                .role(role)
                .allocationPercentage(allocationPercentage)
                .status(TaskAssignmentStatus.ACTIVE)
                .assignedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void unassign() {
        if (this.status == TaskAssignmentStatus.UNASSIGNED) {
            throw new ValidationException("Assignment is already unassigned");
        }

        this.status = TaskAssignmentStatus.UNASSIGNED;
        this.unassignedAt = LocalDateTime.now();
        touch();
    }

    public void updateRole(RoleInTask newRole) {
        validateActive();
        validateRequired(newRole, "Role is required");

        if (this.role == newRole) {
            throw new ValidationException("Assignment already has this role");
        }

        this.role = newRole;
        touch();
    }

    public void updateAllocation(float newAllocation) {
        validateActive();
        validateAllocation(newAllocation);

        this.allocationPercentage = newAllocation;
        touch();
    }

    private void validateActive() {
        if (this.status == TaskAssignmentStatus.UNASSIGNED) {
            throw new ValidationException("Cannot modify an unassigned assignment");
        }
    }

    private static void validateAllocation(float allocation) {
        if (allocation < 0 || allocation > 100) {
            throw new ValidationException("Allocation percentage must be between 0 and 100");
        }
    }

    private static void validateRequired(Object value, String message) {
        if (value == null) {
            throw new ValidationException(message);
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
