package com.dxc.taskservice.application.dto.task.req;

import com.dxc.taskservice.domain.model.valueobject.TaskPriority;
import com.dxc.taskservice.domain.model.valueobject.TaskType;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateTaskRequest(
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters if provided")
    String title,

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    TaskType type,
    TaskPriority priority,
    LocalDateTime startDate,
    LocalDateTime dueDate,
    UUID milestoneId
) {}
