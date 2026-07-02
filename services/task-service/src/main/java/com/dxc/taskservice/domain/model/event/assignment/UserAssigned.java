package com.dxc.taskservice.domain.model.event.assignment;

import com.dxc.taskservice.domain.model.event.DomainEvent;
import com.dxc.taskservice.domain.model.valueobject.RoleInTask;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserAssigned(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        UUID userId,
        RoleInTask role,
        float allocationPercentage,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static UserAssigned now(
            UUID tenantId,
            UUID taskId,
            UUID userId,
            RoleInTask role,
            float allocationPercentage,
            UUID actionBy
    ) {
        return new UserAssigned(
                UUID.randomUUID(),
                tenantId,
                taskId,
                userId,
                role,
                allocationPercentage,
                actionBy,
                LocalDateTime.now()
        );
    }
}
