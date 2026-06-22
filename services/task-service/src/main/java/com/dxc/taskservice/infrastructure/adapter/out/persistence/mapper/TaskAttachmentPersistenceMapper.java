package com.dxc.taskservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.taskservice.domain.model.entity.TaskAttachment;
import com.dxc.taskservice.infrastructure.adapter.out.persistence.entity.TaskAttachmentEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskAttachmentPersistenceMapper {

    public TaskAttachmentEntity toEntity(TaskAttachment domain) {
        if (domain == null) return null;

        return TaskAttachmentEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .taskId(domain.getTaskId())
                .fileUrl(domain.getFileUrl())
                .fileName(domain.getFileName())
                .fileType(domain.getFileType())
                .uploadedBy(domain.getUploadedBy())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public TaskAttachment toDomain(TaskAttachmentEntity entity) {
        if (entity == null) return null;

        return TaskAttachment.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .taskId(entity.getTaskId())
                .fileUrl(entity.getFileUrl())
                .fileName(entity.getFileName())
                .fileType(entity.getFileType())
                .uploadedBy(entity.getUploadedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<TaskAttachmentEntity> toEntityList(List<TaskAttachment> domains) {
        if (domains == null) return null;
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<TaskAttachment> toDomainList(List<TaskAttachmentEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
