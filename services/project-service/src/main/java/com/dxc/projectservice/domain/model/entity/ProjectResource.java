package com.dxc.projectservice.domain.model.entity;

import com.dxc.projectservice.domain.model.valueobject.ResourceType;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProjectResource {
    private UUID id;
    private UUID tenantId;
    private String name;
    private ResourceType type;
    private String url;
    private String description;
    private LocalDateTime createdAt;
}
