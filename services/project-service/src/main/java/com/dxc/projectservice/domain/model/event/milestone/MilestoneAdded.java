package com.dxc.projectservice.domain.model.event.milestone;

import com.dxc.projectservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record MilestoneAdded(
        UUID eventId,
        UUID tenantId,
        UUID projectId,
        String title,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static MilestoneAdded now(
            UUID tenantId,
            UUID projectId,
            String title,
            UUID actionBy
    ) {
        return new MilestoneAdded(
                UUID.randomUUID(),
                tenantId,
                projectId,
                title,
                actionBy,
                LocalDateTime.now());
    }
}
