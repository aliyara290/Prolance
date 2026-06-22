package com.dxc.taskservice.domain.model.event.dependency;

import com.dxc.taskservice.domain.model.event.DomainEvent;
import com.dxc.taskservice.domain.model.valueobject.DependencyType;

import java.time.LocalDateTime;
import java.util.UUID;

public record DependencyAdded(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        UUID dependOnTaskId,
        DependencyType type,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static DependencyAdded now(
            UUID tenantId,
            UUID taskId,
            UUID dependOnTaskId,
            DependencyType type,
            UUID actionBy
    ) {
        return new DependencyAdded(
                UUID.randomUUID(),
                tenantId,
                taskId,
                dependOnTaskId,
                type,
                actionBy,
                LocalDateTime.now()
        );
    }
}
