package com.dxc.projectservice.application.dto.resource.res;

import com.dxc.projectservice.domain.model.valueobject.ResourceType;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResourceResponse(
    UUID id,
    UUID projectId,
    String name,
    String description,
    ResourceType type,
    String url,
    LocalDateTime createdAt
) {}
