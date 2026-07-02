package com.dxc.projectservice.domain.model.event.project;

import com.dxc.projectservice.domain.model.event.DomainEvent;
import com.dxc.projectservice.domain.model.valueobject.ProjectPriority;
import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectCreated(
        UUID eventId,
        UUID tenantId,
        UUID projectId,
        UUID clientId,
        String name,
        ProjectStatus status,
        ProjectPriority priority,
        UUID projectManagerId,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static ProjectCreated now(
            UUID tenantId,
            UUID projectId,
            UUID clientId,
            String name,
            ProjectStatus status,
            ProjectPriority priority,
            UUID projectManagerId,
            UUID actionBy
    ) {
        return new ProjectCreated(
                UUID.randomUUID(),
                tenantId,
                projectId,
                clientId,
                name,
                status,
                priority,
                projectManagerId,
                actionBy,
                LocalDateTime.now()
        );
    }
}
