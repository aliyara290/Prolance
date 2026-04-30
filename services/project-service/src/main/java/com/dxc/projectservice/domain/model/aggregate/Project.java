package com.dxc.projectservice.domain.model.aggregate;

import com.dxc.projectservice.domain.model.valueobject.ProjectPriority;
import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class Project {
    private UUID id;
    private UUID tenantId;
    private UUID clientId;
    private UUID opportunityId;
    private String name;
    private String description;
    private ProjectStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime actuelStartDate;
    private Double estimatedBudget;
    private Double actuelCost;
    private float progressPercentage;
    private UUID projectManagerId;
    private ProjectPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime createdBy;
}