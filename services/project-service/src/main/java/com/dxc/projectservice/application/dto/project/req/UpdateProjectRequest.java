package com.dxc.projectservice.application.dto.project.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
    @NotBlank(message = "name is required")
    @Size(min = 3, max = 100, message = "name must be between 3 and 100 characters")
    String name,
    
    @Size(max = 500, message = "description cannot exceed 500 characters")
    String description
) {}
