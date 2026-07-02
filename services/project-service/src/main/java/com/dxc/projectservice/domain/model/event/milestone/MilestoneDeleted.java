package com.dxc.projectservice.domain.model.event.milestone;

import com.dxc.projectservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record MilestoneDeleted(
        UUID eventId,
        UUID tenantId,
        UUID projectId,
        UUID milestoneId,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static MilestoneDeleted now(
            UUID tenantId,
            UUID projectId,
            UUID milestoneId,
            UUID actionBy
    ) {
        return new MilestoneDeleted(
                UUID.randomUUID(),
                tenantId,
                projectId,
                milestoneId,
                actionBy,
                LocalDateTime.now()
        );
    }
}
