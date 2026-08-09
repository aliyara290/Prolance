package com.dxc.taskservice.domain.model.event.task;

import com.dxc.taskservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDeleted(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        UUID milestoneId,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static TaskDeleted now(
            UUID tenantId,
            UUID taskId,
            UUID milestoneId,
            UUID actionBy
    ) {
        return new TaskDeleted(
                UUID.randomUUID(),
                tenantId,
                taskId,
                milestoneId,
                actionBy,
                LocalDateTime.now()
        );
    }
}
