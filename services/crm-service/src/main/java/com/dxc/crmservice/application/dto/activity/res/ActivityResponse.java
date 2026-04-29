package com.dxc.crmservice.application.dto.activity.res;

import com.dxc.crmservice.domain.model.valueobject.ActivityType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityResponse(
    UUID id,
    ActivityType type,
    String subject,
    String description,
    LocalDateTime scheduledAt,
    LocalDateTime completedAt,
    UUID userId,
    UUID entityId,
    com.dxc.crmservice.domain.model.valueobject.EntityType entityType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
