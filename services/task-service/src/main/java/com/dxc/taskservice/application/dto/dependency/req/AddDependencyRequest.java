package com.dxc.taskservice.application.dto.dependency.req;

import com.dxc.taskservice.domain.model.valueobject.DependencyType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddDependencyRequest(
    @NotNull(message = "Dependent task ID is required")
    UUID dependOnTaskId,

    @NotNull(message = "Dependency type is required")
    DependencyType type
) {}
