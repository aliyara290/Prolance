package com.dxc.crmservice.domain.model.entity;

import com.dxc.crmservice.domain.exception.BusinessRuleViolationException;
import com.dxc.crmservice.domain.exception.ValidationException;
import com.dxc.crmservice.domain.model.valueobject.EntityType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Attachment {

    private final UUID id;
    private final UUID tenantId;

    private final UUID entityId;
    private final EntityType entityType;

    private final UUID uploadedBy;

    private final String fileURL;
    private final String fileName;
    private final String fileType;

    private LocalDateTime createdAt;

    private Attachment(UUID id,
                       UUID tenantId,
                       UUID entityId,
                       EntityType entityType,
                       String fileURL,
                       String fileName,
                       String fileType,
                       UUID uploadedBy) {

        this.id = id == null ? UUID.randomUUID() : id;
        this.tenantId = requireNonNull(tenantId, "tenantId");

        this.entityId = requireNonNull(entityId, "entityId");
        this.entityType = requireNonNull(entityType, "entityType");

        this.uploadedBy = requireNonNull(uploadedBy, "uploadedBy");

        this.fileURL = validateUrl(fileURL);
        this.fileName = validateName(fileName);
        this.fileType = validateFileType(fileType);

        this.createdAt = LocalDateTime.now();
    }

    // Factory
    public static Attachment create(UUID tenantId,
                                    UUID entityId,
                                    EntityType entityType,
                                    String fileURL,
                                    String fileName,
                                    String fileType,
                                    UUID uploadedBy) {

        return new Attachment(
                null,
                tenantId,
                entityId,
                entityType,
                fileURL,
                fileName,
                fileType,
                uploadedBy
        );
    }

    public static Attachment rehydrate(UUID id,
                                       UUID tenantId,
                                       UUID entityId,
                                       EntityType entityType,
                                       String fileURL,
                                       String fileName,
                                       String fileType,
                                       UUID uploadedBy,
                                       LocalDateTime createdAt) {

        Attachment attachment = new Attachment(
                id,
                tenantId,
                entityId,
                entityType,
                fileURL,
                fileName,
                fileType,
                uploadedBy
        );
        attachment.createdAt = createdAt;
        return attachment;
    }

    // rules

    private String validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new ValidationException("File URL cannot be empty");
        }

        try {
            java.net.URL parsedUrl = new java.net.URL(url);

            String protocol = parsedUrl.getProtocol();
            if (!"http".equals(protocol) && !"https".equals(protocol)) {
                throw new ValidationException("URL must use HTTP or HTTPS");
            }

            if (parsedUrl.getHost() == null || parsedUrl.getHost().isBlank()) {
                throw new ValidationException("URL must contain a valid host");
            }

        } catch (Exception e) {
            throw new ValidationException("Invalid file URL");
        }

        return url.trim();
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("File name cannot be empty");
        }
        return name.trim();
    }

    private String validateFileType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new ValidationException("File type cannot be empty");
        }
        return type.trim().toLowerCase();
    }

    private static <T> T requireNonNull(T value, String field) {
        return Objects.requireNonNull(value, field + " cannot be null");
    }

}