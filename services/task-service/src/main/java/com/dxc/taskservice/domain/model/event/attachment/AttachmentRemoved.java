package com.dxc.taskservice.domain.model.event.attachment;

import com.dxc.taskservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttachmentRemoved(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        UUID attachmentId,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static AttachmentRemoved now(
            UUID tenantId,
            UUID taskId,
            UUID attachmentId,
            UUID actionBy
    ) {
        return new AttachmentRemoved(
                UUID.randomUUID(),
                tenantId,
                taskId,
                attachmentId,
                actionBy,
                LocalDateTime.now()
        );
    }
}
