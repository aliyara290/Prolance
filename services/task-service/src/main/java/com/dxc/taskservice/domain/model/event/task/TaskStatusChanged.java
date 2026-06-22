package com.dxc.taskservice.domain.model.event.task;

import com.dxc.taskservice.domain.model.event.DomainEvent;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskStatusChanged(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        TaskStatus oldStatus,
        TaskStatus newStatus,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static TaskStatusChanged now(
            UUID tenantId,
            UUID taskId,
            TaskStatus oldStatus,
            TaskStatus newStatus,
            UUID actionBy
    ) {
        return new TaskStatusChanged(
                UUID.randomUUID(),
                tenantId,
                taskId,
                oldStatus,
                newStatus,
                actionBy,
                LocalDateTime.now()
        );
    }
}
