package com.dxc.taskservice.domain.model.event.comment;

import com.dxc.taskservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentEdited(
        UUID eventId,
        UUID tenantId,
        UUID taskId,
        UUID commentId,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static CommentEdited now(
            UUID tenantId,
            UUID taskId,
            UUID commentId,
            UUID actionBy
    ) {
        return new CommentEdited(
                UUID.randomUUID(),
                tenantId,
                taskId,
                commentId,
                actionBy,
                LocalDateTime.now()
        );
    }
}
