package com.dxc.taskservice.domain.model.event.assignment;

import com.dxc.taskservice.domain.model.event.DomainEvent;
import com.dxc.taskservice.domain.model.valueobject.RoleInTask;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssignmentUpdated(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        UUID userId,
        RoleInTask oldRole,
        RoleInTask newRole,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static AssignmentUpdated now(
            UUID tenantId,
            UUID taskId,
            UUID userId,
            RoleInTask oldRole,
            RoleInTask newRole,
            UUID actionBy
    ) {
        return new AssignmentUpdated(
                UUID.randomUUID(),
                tenantId,
                taskId,
                userId,
                oldRole,
                newRole,
                actionBy,
                LocalDateTime.now()
        );
    }
}
