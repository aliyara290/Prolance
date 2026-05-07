package com.dxc.projectservice.domain.model.event.member;

import com.dxc.projectservice.domain.model.event.DomainEvent;
import com.dxc.projectservice.domain.model.valueobject.MemberRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemberRoleUpdated(
        UUID eventId,
        UUID tenantId,
        UUID projectId,
        UUID userId,
        MemberRole oldRole,
        MemberRole newRole,
        UUID actionBy,
        LocalDateTime occurredOn
) implements DomainEvent {
    public static MemberRoleUpdated now(UUID tenantId, UUID projectId, UUID userId, MemberRole oldRole, MemberRole newRole, UUID actionBy) {
        return new MemberRoleUpdated(UUID.randomUUID(), tenantId, projectId, userId, oldRole, newRole, actionBy, LocalDateTime.now());
    }
}
