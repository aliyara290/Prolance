package com.dxc.taskservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.taskservice.domain.model.entity.TaskComment;
import com.dxc.taskservice.infrastructure.adapter.out.persistence.entity.TaskCommentEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskCommentPersistenceMapper {

    public TaskCommentEntity toEntity(TaskComment domain) {
        if (domain == null) return null;

        return TaskCommentEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .taskId(domain.getTaskId())
                .userId(domain.getUserId())
                .content(domain.getContent())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public TaskComment toDomain(TaskCommentEntity entity) {
        if (entity == null) return null;

        return TaskComment.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .taskId(entity.getTaskId())
                .userId(entity.getUserId())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<TaskCommentEntity> toEntityList(List<TaskComment> domains) {
        if (domains == null) return null;
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<TaskComment> toDomainList(List<TaskCommentEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
