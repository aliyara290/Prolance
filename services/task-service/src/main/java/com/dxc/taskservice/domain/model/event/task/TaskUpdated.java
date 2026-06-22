package com.dxc.taskservice.domain.model.event.task;

import com.dxc.taskservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record TaskUpdated(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        Map<String, Object> changedFields,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static TaskUpdated now(
            UUID tenantId,
            UUID taskId,
            Map<String, Object> changedFields,
            UUID actionBy
    ) {
        return new TaskUpdated(
                UUID.randomUUID(),
                tenantId,
                taskId,
                changedFields,
                actionBy,
                LocalDateTime.now()
        );
    }
}
