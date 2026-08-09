package com.dxc.taskservice.application.service;

import com.dxc.taskservice.application.dto.dependency.req.AddDependencyRequest;
import com.dxc.taskservice.application.dto.dependency.res.TaskDependencyResponse;
import com.dxc.taskservice.application.mapper.TaskDependencyApplicationMapper;
import com.dxc.taskservice.application.port.in.TaskDependencyUseCase;
import com.dxc.taskservice.application.port.out.TaskRepository;
import com.dxc.taskservice.application.security.TenantGuard;
import com.dxc.taskservice.domain.exception.RecordNotFoundException;
import com.dxc.taskservice.domain.model.aggregate.Task;
import com.dxc.taskservice.domain.model.entity.TaskDependency;
import com.dxc.taskservice.infrastructure.config.TenantContextHolder;
import com.dxc.taskservice.application.port.out.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class TaskDependencyService implements TaskDependencyUseCase {

    private final TaskRepository taskRepository;
    private final TenantGuard tenantGuard;
    private final TaskDependencyApplicationMapper dependencyMapper;
    private final DomainEventPublisher eventPublisher;

    @Override
    public TaskDependencyResponse addDependency(UUID taskId, AddDependencyRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();

        // Verify the dependent task exists in the same tenant
        verifyTaskExists(request.dependOnTaskId(), tenantId);

        Task task = loadTask(taskId, tenantId);
        task.addDependency(request.dependOnTaskId(), request.type(), actionBy);
        taskRepository.save(task);
        eventPublisher.publish(task.getDomainEvents());
        task.clearDomainEvents();

        // Return the newly created dependency (last one added)
        TaskDependency created = task.getDependencies().get(task.getDependencies().size() - 1);
        return dependencyMapper.toResponse(created);
    }

    @Override
    public void removeDependency(UUID taskId, UUID dependencyId) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();

        Task task = loadTask(taskId, tenantId);
        task.removeDependency(dependencyId, actionBy);
        taskRepository.save(task);
        eventPublisher.publish(task.getDomainEvents());
        task.clearDomainEvents();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDependencyResponse> getDependencies(UUID taskId) {
        UUID tenantId = getTenantIdAndVerify();
        Task task = loadTask(taskId, tenantId);
        return dependencyMapper.toResponseList(task.getDependencies());
    }

    private Task loadTask(UUID id, UUID tenantId) {
        return taskRepository.findById(id, tenantId)
                .orElseThrow(() -> new RecordNotFoundException("Task not found with id: " + id));
    }

    private void verifyTaskExists(UUID taskId, UUID tenantId) {
        if (!taskRepository.existsById(taskId, tenantId)) {
            throw new RecordNotFoundException("Dependent task not found with id: " + taskId);
        }
    }

    private UUID getTenantIdAndVerify() {
        String tenantIdStr = TenantContextHolder.getTenantId();
        if (tenantIdStr == null) {
            throw new IllegalArgumentException("Tenant context is missing");
        }
        UUID tenantId = UUID.fromString(tenantIdStr);
        tenantGuard.ensureTenantIsActive(tenantId);
        return tenantId;
    }
}
