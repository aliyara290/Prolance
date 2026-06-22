package com.dxc.taskservice.domain.model.event.attachment;

import com.dxc.taskservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttachmentAdded(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        UUID attachmentId,
        String fileName,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static AttachmentAdded now(
            UUID tenantId,
            UUID taskId,
            UUID attachmentId,
            String fileName,
            UUID actionBy
    ) {
        return new AttachmentAdded(
                UUID.randomUUID(),
                tenantId,
                taskId,
                attachmentId,
                fileName,
                actionBy,
                LocalDateTime.now()
        );
    }
}
