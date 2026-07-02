package com.dxc.taskservice.domain.model.event.comment;

import com.dxc.taskservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentRemoved(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        UUID commentId,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static CommentRemoved now(
            UUID tenantId,
            UUID taskId,
            UUID commentId,
            UUID actionBy
    ) {
        return new CommentRemoved(
                UUID.randomUUID(),
                tenantId,
                taskId,
                commentId,
                actionBy,
                LocalDateTime.now()
        );
    }
}
