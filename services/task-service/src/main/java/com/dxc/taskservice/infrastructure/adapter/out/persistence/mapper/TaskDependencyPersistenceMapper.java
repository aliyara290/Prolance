package com.dxc.taskservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.taskservice.domain.model.entity.TaskDependency;
import com.dxc.taskservice.infrastructure.adapter.out.persistence.entity.TaskDependencyEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskDependencyPersistenceMapper {

    public TaskDependencyEntity toEntity(TaskDependency domain) {
        if (domain == null) return null;

        return TaskDependencyEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .taskId(domain.getTaskId())
                .dependOnTaskId(domain.getDependOnTaskId())
                .type(domain.getType())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public TaskDependency toDomain(TaskDependencyEntity entity) {
        if (entity == null) return null;

        return TaskDependency.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .taskId(entity.getTaskId())
                .dependOnTaskId(entity.getDependOnTaskId())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<TaskDependencyEntity> toEntityList(List<TaskDependency> domains) {
        if (domains == null) return null;
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<TaskDependency> toDomainList(List<TaskDependencyEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
