package com.dxc.crmservice.domain.model.entity;

import com.dxc.crmservice.domain.model.valueobject.AuditAction;
import com.dxc.crmservice.domain.model.valueobject.EntityType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AuditLog {
    private UUID id;
    private UUID tenantId;
    private UUID userId;
    private AuditAction action;
    private EntityType entityType;
    private UUID entityId;
    private String message;
    private LocalDateTime createdAt;
}
