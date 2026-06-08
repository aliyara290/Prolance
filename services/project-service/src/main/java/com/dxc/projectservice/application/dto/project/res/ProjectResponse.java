package com.dxc.projectservice.application.dto.project.res;

import com.dxc.projectservice.domain.model.valueobject.ProjectPriority;
import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponse(
    UUID id,
    UUID clientId,
    UUID ownerId,
    UUID opportunityId,
    String name,
    String description,
    String prefix,
    ProjectStatus status,
    ProjectPriority priority,
    LocalDateTime plannedStartDate,
    LocalDateTime plannedEndDate,
    LocalDateTime actualStartDate,
    LocalDateTime actualEndDate,
    BigDecimal estimatedBudget,
    BigDecimal actualCost,
    float progress,
    UUID projectManagerId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
