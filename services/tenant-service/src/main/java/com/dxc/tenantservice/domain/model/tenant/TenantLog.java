package com.dxc.tenantservice.domain.model.tenant;

import com.dxc.tenantservice.domain.model.enums.EntityType;
import com.dxc.tenantservice.domain.model.enums.TenantLogAction;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TenantLog {

    private final UUID id;
    private final UUID tenantId;
    private final UUID userId;

    private final TenantLogAction action;

    private final EntityType entityType;
    private final UUID entityId;

    private final String ipAddress;
    private final String userAgent;

    private final LocalDateTime createdAt;

    public TenantLog(
            UUID id,
            UUID tenantId,
            UUID userId,
            TenantLogAction action,
            EntityType entityType,
            UUID entityId,
            String ipAddress,
            String userAgent,
            LocalDateTime createdAt
    ) {
        validate(tenantId, action, entityType);

        this.id = id;
        this.tenantId = tenantId;
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
    }

    public static TenantLog create(
            UUID tenantId,
            UUID userId,
            TenantLogAction action,
            EntityType entityType,
            UUID entityId,
            String ipAddress,
            String userAgent
    ) {
        return new TenantLog(
                UUID.randomUUID(),
                tenantId,
                userId,
                action,
                entityType,
                entityId,
                ipAddress,
                userAgent,
                LocalDateTime.now()
        );
    }

    private void validate(UUID tenantId, TenantLogAction action, EntityType entityType) {
        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId is required");
        }
        if (action == null) {
            throw new IllegalArgumentException("action is required");
        }
        if (entityType == null) {
            throw new IllegalArgumentException("entityType is required");
        }
    }
}