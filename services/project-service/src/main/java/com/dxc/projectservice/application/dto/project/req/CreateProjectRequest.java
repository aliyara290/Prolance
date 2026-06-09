package com.dxc.projectservice.application.dto.project.req;

import com.dxc.projectservice.domain.model.valueobject.ProjectPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateProjectRequest(
    @NotNull(message = "clientId is required")
    UUID clientId,
    
    UUID opportunityId,
    
    @NotBlank(message = "name is required")
    @Size(min = 3, max = 100, message = "name must be between 3 and 100 characters")
    String name,
    
    @Size(max = 500, message = "description cannot exceed 500 characters")
    String description,
    
    @NotNull(message = "priority is required")
    ProjectPriority priority,
    
    @NotNull(message = "plannedStartDate is required")
    LocalDateTime plannedStartDate,
    
    @NotNull(message = "plannedEndDate is required")
    LocalDateTime plannedEndDate,
    
    @PositiveOrZero(message = "estimatedBudget must be zero or positive")
    BigDecimal estimatedBudget,
    
    @NotNull(message = "projectManagerId is required")
    UUID projectManagerId
) {}