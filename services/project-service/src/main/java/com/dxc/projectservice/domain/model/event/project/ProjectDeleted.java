package com.dxc.projectservice.domain.model.event.project;

import com.dxc.projectservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectDeleted(
        UUID eventId,
        UUID tenantId,
        UUID projectId,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static ProjectDeleted now(
            UUID tenantId,
            UUID projectId,
            UUID actionBy
    ) {
        return new ProjectDeleted(
                UUID.randomUUID(),
                tenantId,
                projectId,
                actionBy,
                LocalDateTime.now()
        );
    }
}
