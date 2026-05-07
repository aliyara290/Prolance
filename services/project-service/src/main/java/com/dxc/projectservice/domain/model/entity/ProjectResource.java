package com.dxc.projectservice.domain.model.entity;

import com.dxc.projectservice.domain.model.valueobject.ResourceType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectResource {
    private final UUID id;
    private final UUID tenantId;
    private UUID projectId;
    private String name;
    private ResourceType type;
    private String url;
    private String description;
    private final LocalDateTime createdAt;

    public static ProjectResource create(UUID tenantId, String name, ResourceType type, String url, String description) {
        return ProjectResource.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .type(type)
                .url(url)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
