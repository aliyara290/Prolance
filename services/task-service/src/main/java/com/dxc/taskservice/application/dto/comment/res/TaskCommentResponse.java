package com.dxc.taskservice.application.dto.comment.res;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskCommentResponse(
    UUID id,
    UUID taskId,
    UUID userId,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
