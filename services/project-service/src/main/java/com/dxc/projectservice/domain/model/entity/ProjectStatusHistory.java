package com.dxc.projectservice.domain.model.entity;

import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProjectStatusHistory {
    private UUID id;
    private UUID tenantId;
    private ProjectStatus oldStatus;
    private ProjectStatus newStatus;
    private UUID changedBy;
    private LocalDateTime changedAt;
    private String comment;
}
