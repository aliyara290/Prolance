package com.dxc.attachmentservice.domain.model.entity;

import com.dxc.attachmentservice.domain.model.valueobject.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Attachment {

    private final UUID id;
    private final UUID tenantId;
    private final EntityType entityType;
    private final UUID entityId;
    private final String originalFileName;
    private final String storedFileName;
    private final String objectKey;
    private final String bucketName;
    private final String contentType;
    private final long size;
    private final UUID uploadedBy;
    private final LocalDateTime uploadedAt;
    private boolean deleted;

    public static Attachment create(
            UUID tenantId,
            EntityType entityType,
            UUID entityId,
            String originalFileName,
            String storedFileName,
            String objectKey,
            String bucketName,
            String contentType,
            long size,
            UUID uploadedBy
    ) {
        validate(tenantId, entityType, entityId, originalFileName, objectKey, bucketName, uploadedBy);

        return Attachment.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .entityType(entityType)
                .entityId(entityId)
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .objectKey(objectKey)
                .bucketName(bucketName)
                .contentType(contentType)
                .size(size)
                .uploadedBy(uploadedBy)
                .uploadedAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    public void markDeleted() {
        this.deleted = true;
    }

    private static void validate(
            UUID tenantId,
            EntityType entityType,
            UUID entityId,
            String originalFileName,
            String objectKey,
            String bucketName,
            UUID uploadedBy
    ) {
        require(tenantId, "Tenant ID is required");
        require(entityType, "Entity type is required");
        require(entityId, "Entity ID is required");
        require(originalFileName, "Original file name is required");
        require(objectKey, "Object key is required");
        require(bucketName, "Bucket name is required");
        require(uploadedBy, "Uploader ID is required");
    }

    private static void require(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        if (value instanceof String s && s.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
