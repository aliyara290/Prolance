package com.dxc.taskservice.application.dto.dependency.res;

import com.dxc.taskservice.domain.model.valueobject.DependencyType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDependencyResponse(
    UUID id,
    UUID taskId,
    UUID dependOnTaskId,
    DependencyType type,
    LocalDateTime createdAt
) {}
