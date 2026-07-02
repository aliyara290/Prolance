package com.dxc.taskservice.domain.model.event.dependency;

import com.dxc.taskservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record DependencyRemoved(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        UUID dependOnTaskId,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static DependencyRemoved now(
            UUID tenantId,
            UUID taskId,
            UUID dependOnTaskId,
            UUID actionBy
    ) {
        return new DependencyRemoved(
                UUID.randomUUID(),
                tenantId,
                taskId,
                dependOnTaskId,
                actionBy,
                LocalDateTime.now()
        );
    }
}
