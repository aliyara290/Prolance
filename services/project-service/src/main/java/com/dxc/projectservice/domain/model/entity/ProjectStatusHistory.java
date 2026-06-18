package com.dxc.projectservice.domain.model.entity;

import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ProjectStatusHistory {
    private final UUID id;
    private final UUID tenantId;
    private UUID projectId;
    private final ProjectStatus oldStatus;
    private final ProjectStatus newStatus;
    private final UUID changedBy;
    private final LocalDateTime changedAt;
    private final String comment;

    public static ProjectStatusHistory create(UUID tenantId, ProjectStatus oldStatus, ProjectStatus newStatus, UUID changedBy, String comment) {
        return ProjectStatusHistory.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .changedAt(LocalDateTime.now())
                .comment(comment)
                .build();
    }
}