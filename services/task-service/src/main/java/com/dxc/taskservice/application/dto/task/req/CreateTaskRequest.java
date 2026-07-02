package com.dxc.taskservice.application.dto.task.req;

import com.dxc.taskservice.application.dto.assignment.req.AssignUserRequest;
import com.dxc.taskservice.application.dto.attachment.req.AddAttachmentRequest;
import com.dxc.taskservice.application.dto.dependency.req.AddDependencyRequest;
import com.dxc.taskservice.domain.model.valueobject.TaskPriority;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;
import com.dxc.taskservice.domain.model.valueobject.TaskType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateTaskRequest(
    @NotNull(message = "Project ID is required")
    UUID projectId,

    UUID milestoneId,

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    String title,

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    @NotNull(message = "Task type is required")
    TaskType type,

    TaskStatus status,

    @NotNull(message = "Task priority is required")
    TaskPriority priority,

    @NotNull(message = "Start date is required")
    LocalDateTime startDate,

    LocalDateTime dueDate,
    UUID reporterId,

    @Valid List<AssignUserRequest> assignments,
    @Valid List<AddDependencyRequest> dependencies,
    @Valid List<AddAttachmentRequest> attachments
) {}
