package com.dxc.taskservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.taskservice.domain.model.valueobject.DependencyType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_dependencies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDependencyEntity {
    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @Column(name = "task_id", nullable = false, updatable = false)
    private UUID taskId;

    @Column(name = "depend_on_task_id", nullable = false, updatable = false)
    private UUID dependOnTaskId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DependencyType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
