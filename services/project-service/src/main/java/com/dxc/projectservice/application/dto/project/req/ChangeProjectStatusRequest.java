package com.dxc.projectservice.application.dto.project.req;

import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeProjectStatusRequest(
    @NotNull(message = "newStatus is required")
    ProjectStatus newStatus,
    
    @Size(max = 255, message = "comment cannot exceed 255 characters")
    String comment
) {}
