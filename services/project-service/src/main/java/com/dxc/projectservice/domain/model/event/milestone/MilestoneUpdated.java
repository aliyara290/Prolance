package com.dxc.projectservice.domain.model.event.milestone;

import com.dxc.projectservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record MilestoneUpdated(
        UUID eventId,
        UUID tenantId,
        UUID projectId,
        UUID milestoneId,
        Map<String, Object> payload,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static MilestoneUpdated now(
            UUID tenantId,
            UUID projectId,
            UUID milestoneId,
            Map<String, Object> payload,
            UUID actionBy
    ) {
        return new MilestoneUpdated(
                UUID.randomUUID(),
                tenantId,
                projectId,
                milestoneId,
                payload,
                actionBy,
                LocalDateTime.now()
        );
    }
}
