package com.dxc.attachmentservice.application.dto.req;

import com.dxc.attachmentservice.domain.model.valueobject.EntityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PresignedUploadRequest(
    @NotNull(message = "Entity type is required")
    EntityType entityType,

    @NotNull(message = "Entity ID is required")
    UUID entityId,

    @NotBlank(message = "File name is required")
    String fileName,

    @NotBlank(message = "Content type is required")
    String contentType
) {}
