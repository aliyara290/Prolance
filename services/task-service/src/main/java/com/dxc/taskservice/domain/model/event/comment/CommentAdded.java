package com.dxc.taskservice.domain.model.event.comment;

import com.dxc.taskservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentAdded(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        UUID commentId,
        UUID userId,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static CommentAdded now(
            UUID tenantId,
            UUID taskId,
            UUID commentId,
            UUID userId,
            UUID actionBy
    ) {
        return new CommentAdded(
                UUID.randomUUID(),
                tenantId,
                taskId,
                commentId,
                userId,
                actionBy,
                LocalDateTime.now()
        );
    }
}
