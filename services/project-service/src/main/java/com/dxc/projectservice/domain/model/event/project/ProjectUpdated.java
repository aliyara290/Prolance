package com.dxc.projectservice.domain.model.event.project;

import com.dxc.projectservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record ProjectUpdated(
        UUID eventId,
        UUID tenantId,
        UUID projectId,
        Map<String, Object> payload,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static ProjectUpdated now(
            UUID tenantId,
            UUID projectId,
            Map<String, Object> payload,
            UUID actionBy
    ) {
        return new ProjectUpdated(
                UUID.randomUUID(),
                tenantId,
                projectId,
                payload,
                actionBy,
                LocalDateTime.now()
        );
    }
}
