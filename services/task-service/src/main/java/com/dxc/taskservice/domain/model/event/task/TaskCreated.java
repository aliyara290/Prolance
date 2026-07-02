package com.dxc.taskservice.domain.model.event.task;

import com.dxc.taskservice.domain.model.event.DomainEvent;
import com.dxc.taskservice.domain.model.valueobject.TaskPriority;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;
import com.dxc.taskservice.domain.model.valueobject.TaskType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskCreated(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        UUID projectId,
        String title,
        TaskType type,
        TaskPriority priority,
        TaskStatus status,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static TaskCreated now(
            UUID tenantId,
            UUID taskId,
            UUID projectId,
            String title,
            TaskType type,
            TaskPriority priority,
            TaskStatus status,
            UUID actionBy
    ) {
        return new TaskCreated(
                UUID.randomUUID(),
                tenantId,
                taskId,
                projectId,
                title,
                type,
                priority,
                status,
                actionBy,
                LocalDateTime.now()
        );
    }
}
