package com.dxc.taskservice.domain.model.entity;

import com.dxc.taskservice.domain.model.valueobject.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TaskStatusHistory {

    private final UUID id;
    private final UUID tenantId;
    private final UUID taskId;
    private final TaskStatus oldStatus;
    private final TaskStatus newStatus;
    private final UUID changedBy;
    private final LocalDateTime changedAt;
    private final String comment;

    public static TaskStatusHistory create(
            UUID tenantId,
            UUID taskId,
            TaskStatus oldStatus,
            TaskStatus newStatus,
            UUID changedBy,
            String comment
    ) {
        return TaskStatusHistory.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .taskId(taskId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .changedAt(LocalDateTime.now())
                .comment(comment)
                .build();
    }
}
