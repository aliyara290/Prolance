package com.dxc.projectservice.domain.model.entity;

import com.dxc.projectservice.domain.model.valueobject.ResourceType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ProjectResource {
    private final UUID id;
    private final UUID tenantId;
    private UUID projectId;
    private String name;
    private ResourceType type;
    private String url;
    private String description;
    private final LocalDateTime createdAt;
    private final UUID createdBy;

    public static ProjectResource create(UUID tenantId, String name, ResourceType type, String url, String description, UUID createdBy) {
        return ProjectResource.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .type(type)
                .url(url)
                .description(description)
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
    }
}
