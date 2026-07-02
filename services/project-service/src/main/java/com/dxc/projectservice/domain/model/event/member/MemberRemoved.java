package com.dxc.projectservice.domain.model.event.member;

import com.dxc.projectservice.domain.model.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemberRemoved(
        UUID eventId,
        UUID tenantId,
        UUID projectId,
        UUID memberId,
        UUID actionBy,
        LocalDateTime occurredOn

) implements DomainEvent {
    public static MemberRemoved now(UUID tenantId, UUID projectId, UUID memberId, UUID actionBy) {
        return new MemberRemoved(UUID.randomUUID(), tenantId, projectId, memberId, actionBy, LocalDateTime.now());
    }
}