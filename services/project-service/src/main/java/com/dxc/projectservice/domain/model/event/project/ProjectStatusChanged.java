package com.dxc.projectservice.domain.model.event.project;

import com.dxc.projectservice.domain.model.event.DomainEvent;
import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectStatusChanged(
        UUID eventId,
        UUID tenantId,
        UUID projectId,
        ProjectStatus oldStatus,
        ProjectStatus newStatus,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static ProjectStatusChanged now(
            UUID tenantId,
            UUID projectId,
            ProjectStatus oldStatus,
            ProjectStatus newStatus,
            UUID actionBy
    ) {
        return new ProjectStatusChanged(
                UUID.randomUUID(),
                tenantId,
                projectId,
                oldStatus,
                newStatus,
                actionBy,
                LocalDateTime.now()
        );
    }
}