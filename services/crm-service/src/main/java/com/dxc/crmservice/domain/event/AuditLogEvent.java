package com.dxc.crmservice.domain.event;

import com.dxc.crmservice.domain.model.valueobject.AuditAction;
import com.dxc.crmservice.domain.model.valueobject.EntityType;

import java.util.UUID;

public record AuditLogEvent(
        UUID tenantId,
        UUID userId,
        AuditAction action,
        EntityType entityType,
        UUID entityId,
        String message
) {}
