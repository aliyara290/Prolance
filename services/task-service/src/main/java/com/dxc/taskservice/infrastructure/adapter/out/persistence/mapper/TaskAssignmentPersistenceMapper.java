package com.dxc.taskservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.taskservice.domain.model.entity.TaskAssignment;
import com.dxc.taskservice.infrastructure.adapter.out.persistence.entity.TaskAssignmentEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskAssignmentPersistenceMapper {

    public TaskAssignmentEntity toEntity(TaskAssignment domain) {
        if (domain == null) return null;

        return TaskAssignmentEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .taskId(domain.getTaskId())
                .userId(domain.getUserId())
                .role(domain.getRole())
                .allocationPercentage(domain.getAllocationPercentage())
                .status(domain.getStatus())
                .assignedAt(domain.getAssignedAt())
                .unassignedAt(domain.getUnassignedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public TaskAssignment toDomain(TaskAssignmentEntity entity) {
        if (entity == null) return null;

        return TaskAssignment.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .taskId(entity.getTaskId())
                .userId(entity.getUserId())
                .role(entity.getRole())
                .allocationPercentage(entity.getAllocationPercentage())
                .status(entity.getStatus())
                .assignedAt(entity.getAssignedAt())
                .unassignedAt(entity.getUnassignedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<TaskAssignmentEntity> toEntityList(List<TaskAssignment> domains) {
        if (domains == null) return null;
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<TaskAssignment> toDomainList(List<TaskAssignmentEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
