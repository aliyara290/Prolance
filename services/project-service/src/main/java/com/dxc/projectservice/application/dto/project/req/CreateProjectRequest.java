package com.dxc.projectservice.application.dto.project.req;

import com.dxc.projectservice.domain.model.valueobject.ProjectPriority;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateProjectRequest(
    UUID clientId,
    UUID opportunityId,
    String name,
    String description,
    ProjectPriority priority,
    LocalDateTime plannedStartDate,
    LocalDateTime plannedEndDate,
    BigDecimal estimatedBudget,
    UUID projectManagerId
) {}
