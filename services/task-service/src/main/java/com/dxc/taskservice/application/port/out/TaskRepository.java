package com.dxc.taskservice.application.port.out;

import com.dxc.taskservice.domain.model.aggregate.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskRepository extends GenericRepository<Task> {
    Page<Task> findByProjectId(UUID projectId, UUID tenantId, Pageable pageable);
    Page<Task> findByMilestoneId(UUID milestoneId, UUID tenantId, Pageable pageable);
    Page<Task> findByAssigneeId(UUID assigneeId, UUID tenantId, Pageable pageable);
}
