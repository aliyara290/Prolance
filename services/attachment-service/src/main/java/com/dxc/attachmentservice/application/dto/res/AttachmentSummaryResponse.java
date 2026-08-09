package com.dxc.attachmentservice.application.dto.res;

import com.dxc.attachmentservice.domain.model.valueobject.EntityType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttachmentSummaryResponse(
    UUID id,
    EntityType entityType,
    UUID entityId,
    String originalFileName,
    String contentType,
    long size,
    UUID uploadedBy,
    LocalDateTime uploadedAt
) {}
