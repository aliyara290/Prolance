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
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
