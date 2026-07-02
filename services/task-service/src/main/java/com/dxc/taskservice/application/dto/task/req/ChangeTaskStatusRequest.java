package com.dxc.taskservice.application.dto.task.req;

import com.dxc.taskservice.domain.model.valueobject.TaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeTaskStatusRequest(
    @NotNull(message = "New status is required")
    TaskStatus newStatus,

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    String comment
) {}
