package com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.crmservice.domain.model.entity.Attachment;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.AttachmentEntity;
import org.springframework.stereotype.Component;

@Component
public class AttachmentPersistenceMapper {

    public AttachmentEntity toEntity(Attachment domain) {
        if (domain == null) return null;
        return AttachmentEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .entityId(domain.getEntityId())
                .entityType(domain.getEntityType())
                .uploadedBy(domain.getUploadedBy())
                .fileURL(domain.getFileURL())
                .fileName(domain.getFileName())
                .fileType(domain.getFileType())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public Attachment toDomain(AttachmentEntity entity) {
        if (entity == null) return null;
        return Attachment.rehydrate(
                entity.getId(),
                entity.getTenantId(),
                entity.getEntityId(),
                entity.getEntityType(),
                entity.getFileURL(),
                entity.getFileName(),
                entity.getFileType(),
                entity.getUploadedBy(),
                entity.getCreatedAt()
        );
    }
}
