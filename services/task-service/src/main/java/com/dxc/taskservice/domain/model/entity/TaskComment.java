package com.dxc.taskservice.domain.model.entity;

import com.dxc.taskservice.domain.exception.BusinessRuleException;
import com.dxc.taskservice.domain.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TaskComment {

    private final UUID id;
    private final UUID tenantId;
    private final UUID taskId;
    private final UUID userId;

    private String content;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskComment create(
            UUID tenantId,
            UUID taskId,
            UUID userId,
            String content
    ) {
        validateRequired(tenantId, "Tenant ID is required");
        validateRequired(taskId, "Task ID is required");
        validateRequired(userId, "User ID is required");
        validateContent(content);

        LocalDateTime now = LocalDateTime.now();

        return TaskComment.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .taskId(taskId)
                .userId(userId)
                .content(content.trim())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void editContent(String newContent, UUID editorUserId) {
        if (!this.userId.equals(editorUserId)) {
            throw new BusinessRuleException("Only the author can edit their comment");
        }

        validateContent(newContent);
        this.content = newContent.trim();
        touch();
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new ValidationException("Comment content cannot be empty");
        }
    }

    private static void validateRequired(Object value, String message) {
        if (value == null) {
            throw new ValidationException(message);
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
