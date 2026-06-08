package com.dxc.projectservice.domain.model.event.member;

import com.dxc.projectservice.domain.model.event.DomainEvent;
import com.dxc.projectservice.domain.model.valueobject.MemberRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemberAdded(
        UUID eventId,
        UUID tenantId,
        UUID projectId,
        UUID userId,
        MemberRole role,
        int allocation,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static MemberAdded now(
            UUID tenantId,
            UUID projectId,
            UUID userId,
            MemberRole role,
            int allocation,
            UUID actionBy
    ) {
        return new MemberAdded(
                UUID.randomUUID(),
                tenantId,
                projectId,
                userId,
                role,
                allocation,
                actionBy,
                LocalDateTime.now()
        );
    }
}