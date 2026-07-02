package com.dxc.projectservice.domain.model.event.resource;

import com.dxc.projectservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResourceAdded(
        UUID eventId,
        UUID tenantId,
        UUID projectId,
        UUID resourceId,
        String name,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static ResourceAdded now(
            UUID tenantId,
            UUID projectId,
            UUID resourceId,
            String name,
            UUID actionBy
    ) {
        return new ResourceAdded(
                UUID.randomUUID(),
                tenantId,
                projectId,
                resourceId,
                name,
                actionBy,
                LocalDateTime.now()
        );
    }
}
