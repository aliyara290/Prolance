package com.dxc.taskservice.domain.model.entity;

import com.dxc.taskservice.domain.exception.ValidationException;
import com.dxc.taskservice.domain.model.valueobject.DependencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TaskDependency {

    private final UUID id;
    private final UUID tenantId;
    private final UUID taskId;
    private final UUID dependOnTaskId;
    private final DependencyType type;
    private final LocalDateTime createdAt;

    public static TaskDependency create(
            UUID tenantId,
            UUID taskId,
            UUID dependOnTaskId,
            DependencyType type
    ) {
        validateRequired(tenantId, "Tenant ID is required");
        validateRequired(taskId, "Task ID is required");
        validateRequired(dependOnTaskId, "Depend-on Task ID is required");
        validateRequired(type, "Dependency type is required");

        if (taskId.equals(dependOnTaskId)) {
            throw new ValidationException("A task cannot depend on itself");
        }

        return TaskDependency.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .taskId(taskId)
                .dependOnTaskId(dependOnTaskId)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static void validateRequired(Object value, String message) {
        if (value == null) {
            throw new ValidationException(message);
        }
    }
}
