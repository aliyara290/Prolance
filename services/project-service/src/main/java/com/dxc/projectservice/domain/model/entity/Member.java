package com.dxc.projectservice.domain.model.entity;

import com.dxc.projectservice.domain.exception.ValidationException;
import com.dxc.projectservice.domain.model.valueobject.MemberRole;
import com.dxc.projectservice.domain.model.valueobject.MemberStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Member {

    private final UUID id;
    private final UUID tenantId;
    private final UUID projectId;
    private final UUID userId;

    private MemberRole role;
    private int allocationPercentage;
    private MemberStatus status;

    private final LocalDateTime joinedAt;
    private LocalDateTime leftAt;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;

    public static Member create(
            UUID tenantId,
            UUID projectId,
            UUID userId,
            MemberRole role,
            int allocationPercentage,
            UUID createdBy
    ) {
        validateRequired(tenantId, "Tenant id is required");
        validateRequired(projectId, "Project id is required");
        validateRequired(userId, "User id is required");
        validateRequired(role, "Member role is required");
        validateRequired(createdBy, "Created by is required");
        validateAllocation(allocationPercentage);

        LocalDateTime now = LocalDateTime.now();

        return Member.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .projectId(projectId)
                .userId(userId)
                .role(role)
                .allocationPercentage(allocationPercentage)
                .status(MemberStatus.ACTIVE)
                .createdBy(createdBy)
                .joinedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateRole(MemberRole newRole) {
        validateActive();
        validateRequired(newRole, "Member role is required");

        if (this.role == newRole) {
            throw new ValidationException("Member already has this role");
        }

        this.role = newRole;

        touch();
    }

    public void updateAllocation(int newAllocation) {
        validateActive();
        validateAllocation(newAllocation);

        this.allocationPercentage = newAllocation;

        touch();
    }

    public void remove() {
        if (this.status == MemberStatus.REMOVED) {
            throw new ValidationException("Member is already removed");
        }

        this.status = MemberStatus.REMOVED;
        this.leftAt = LocalDateTime.now();
        touch();
    }

    public void deactivate() {
        validateActive();

        LocalDateTime now = LocalDateTime.now();

        this.status = MemberStatus.INACTIVE;
        touch();
    }

    private void validateActive() {
        if (this.status == MemberStatus.INACTIVE) {
            throw new ValidationException("Member is inactive");
        }
    }

    private static void validateAllocation(int allocation) {
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