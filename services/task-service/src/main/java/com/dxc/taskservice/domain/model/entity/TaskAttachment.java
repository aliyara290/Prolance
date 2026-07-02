package com.dxc.taskservice.domain.model.entity;

import com.dxc.taskservice.domain.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TaskAttachment {

    private final UUID id;
    private final UUID tenantId;
    private final UUID taskId;

    private final String fileUrl;
    private final String fileName;
    private final String fileType;
    private final UUID uploadedBy;

    private final LocalDateTime createdAt;

    public static TaskAttachment create(
            UUID tenantId,
            UUID taskId,
            String fileUrl,
            String fileName,
            String fileType,
            UUID uploadedBy
    ) {
        validateRequired(tenantId, "Tenant ID is required");
        validateRequired(taskId, "Task ID is required");
        validateRequired(fileUrl, "File URL is required");
        validateRequired(fileName, "File name is required");
        validateRequired(uploadedBy, "Uploaded by is required");

        return TaskAttachment.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .taskId(taskId)
                .fileUrl(fileUrl)
                .fileName(fileName)
                .fileType(fileType)
                .uploadedBy(uploadedBy)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static void validateRequired(Object value, String message) {
        if (value == null) {
            throw new ValidationException(message);
        }
    }
}
