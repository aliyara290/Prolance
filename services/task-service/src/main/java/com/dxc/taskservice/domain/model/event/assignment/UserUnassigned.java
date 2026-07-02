package com.dxc.taskservice.domain.model.event.assignment;

import com.dxc.taskservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserUnassigned(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        UUID userId,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static UserUnassigned now(
            UUID tenantId,
            UUID taskId,
            UUID userId,
            UUID actionBy
    ) {
        return new UserUnassigned(
                UUID.randomUUID(),
                tenantId,
                taskId,
                userId,
                actionBy,
                LocalDateTime.now()
        );
    }
}
