package com.dxc.attachmentservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.attachmentservice.domain.model.entity.Attachment;
import com.dxc.attachmentservice.infrastructure.adapter.out.persistence.entity.AttachmentEntity;
import org.springframework.stereotype.Component;

@Component
public class AttachmentPersistenceMapper {

    public AttachmentEntity toEntity(Attachment domain) {
        if (domain == null) return null;

        return AttachmentEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .entityType(domain.getEntityType())
                .entityId(domain.getEntityId())
                .originalFileName(domain.getOriginalFileName())
                .storedFileName(domain.getStoredFileName())
                .objectKey(domain.getObjectKey())
                .bucketName(domain.getBucketName())
                .contentType(domain.getContentType())
                .size(domain.getSize())
                .uploadedBy(domain.getUploadedBy())
                .uploadedAt(domain.getUploadedAt())
                .deleted(domain.isDeleted())
                .build();
    }

    public Attachment toDomain(AttachmentEntity entity) {
        if (entity == null) return null;

        return Attachment.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .originalFileName(entity.getOriginalFileName())
                .storedFileName(entity.getStoredFileName())
                .objectKey(entity.getObjectKey())
                .bucketName(entity.getBucketName())
                .contentType(entity.getContentType())
                .size(entity.getSize())
                .uploadedBy(entity.getUploadedBy())
                .uploadedAt(entity.getUploadedAt())
                .deleted(entity.isDeleted())
                .build();
    }
}
