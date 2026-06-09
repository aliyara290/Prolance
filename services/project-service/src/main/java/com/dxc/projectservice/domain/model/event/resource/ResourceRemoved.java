package com.dxc.projectservice.domain.model.event.resource;

import com.dxc.projectservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResourceRemoved(
        UUID eventId,
        UUID tenantId,
        UUID projectId,
        UUID resourceId,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static ResourceRemoved now(
            UUID tenantId,
            UUID projectId,
            UUID resourceId,
            UUID actionBy
    ) {
        return new ResourceRemoved(
                UUID.randomUUID(),
                tenantId,
                projectId,
                resourceId,
                actionBy,
                LocalDateTime.now()
        );
    }
}
