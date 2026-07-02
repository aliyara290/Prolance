package com.dxc.taskservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.taskservice.domain.model.entity.TaskStatusHistory;
import com.dxc.taskservice.infrastructure.adapter.out.persistence.entity.TaskStatusHistoryEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskStatusHistoryPersistenceMapper {

    public TaskStatusHistoryEntity toEntity(TaskStatusHistory domain) {
        if (domain == null) return null;

        return TaskStatusHistoryEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .taskId(domain.getTaskId())
                .oldStatus(domain.getOldStatus())
                .newStatus(domain.getNewStatus())
                .changedBy(domain.getChangedBy())
                .changedAt(domain.getChangedAt())
                .comment(domain.getComment())
                .build();
    }

    public TaskStatusHistory toDomain(TaskStatusHistoryEntity entity) {
        if (entity == null) return null;

        return TaskStatusHistory.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .taskId(entity.getTaskId())
                .oldStatus(entity.getOldStatus())
                .newStatus(entity.getNewStatus())
                .changedBy(entity.getChangedBy())
                .changedAt(entity.getChangedAt())
                .comment(entity.getComment())
                .build();
    }

    public List<TaskStatusHistoryEntity> toEntityList(List<TaskStatusHistory> domains) {
        if (domains == null) return null;
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<TaskStatusHistory> toDomainList(List<TaskStatusHistoryEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
