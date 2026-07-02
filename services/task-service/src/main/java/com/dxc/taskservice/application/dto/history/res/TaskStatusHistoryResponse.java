package com.dxc.taskservice.application.dto.history.res;

import com.dxc.taskservice.domain.model.valueobject.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskStatusHistoryResponse(
    UUID id,
    UUID taskId,
    TaskStatus oldStatus,
    TaskStatus newStatus,
    UUID changedBy,
    LocalDateTime changedAt,
    String comment
) {}
