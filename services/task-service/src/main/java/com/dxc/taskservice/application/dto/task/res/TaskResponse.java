package com.dxc.taskservice.application.dto.task.res;

import com.dxc.taskservice.application.dto.assignment.res.TaskAssignmentResponse;
import com.dxc.taskservice.application.dto.attachment.res.TaskAttachmentResponse;
import com.dxc.taskservice.application.dto.comment.res.TaskCommentResponse;
import com.dxc.taskservice.application.dto.dependency.res.TaskDependencyResponse;
import com.dxc.taskservice.application.dto.history.res.TaskStatusHistoryResponse;
import com.dxc.taskservice.domain.model.valueobject.TaskPriority;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;
import com.dxc.taskservice.domain.model.valueobject.TaskType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TaskResponse (
    UUID id,
    UUID projectId,
    UUID milestoneId,
    String title,
    String description,
    TaskType type,
    TaskPriority priority,
    TaskStatus status,
    LocalDateTime startDate,
    LocalDateTime dueDate,
    LocalDateTime completedAt,
    UUID createdBy,
    UUID reporterId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,

    List<TaskAssignmentResponse> assignments,
    List<TaskCommentResponse> comments,
    List<TaskAttachmentResponse> attachments,
    List<TaskDependencyResponse> dependencies,
    List<TaskStatusHistoryResponse> statusHistory
) {}
