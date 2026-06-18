package com.dxc.projectservice.application.dto.project.req;

import com.dxc.projectservice.domain.model.valueobject.ProjectPriority;
import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateProjectRequest(
    UUID clientId,
    UUID opportunityId,
    @NotBlank(message = "name is required")
    @Size(min = 3, max = 100, message = "name must be between 3 and 100 characters")
    String name,
    @Size(max = 500, message = "description cannot exceed 500 characters")
    String description,
    ProjectPriority priority,
    LocalDateTime plannedStartDate,
    LocalDateTime plannedEndDate,
    BigDecimal estimatedBudget,
    UUID projectManagerId,
    ProjectStatus status
) {}
