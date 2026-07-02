package com.dxc.projectservice.application.dto.resource.req;

import com.dxc.projectservice.domain.model.valueobject.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddResourceRequest(
    @NotBlank(message = "name is required")
    @Size(min = 3, max = 100, message = "name must be between 3 and 100 characters")
    String name,
    
    @Size(max = 500, message = "description cannot exceed 500 characters")
    String description,
    
    @NotNull(message = "type is required")
    ResourceType type,
    
    @NotBlank(message = "url is required")
    String url
) {}
