package com.dxc.taskservice.application.service;

import com.dxc.taskservice.application.dto.assignment.req.AssignUserRequest;
import com.dxc.taskservice.application.dto.assignment.req.UpdateAssignmentRequest;
import com.dxc.taskservice.application.dto.assignment.res.TaskAssignmentResponse;
import com.dxc.taskservice.application.mapper.TaskAssignmentApplicationMapper;
import com.dxc.taskservice.application.port.in.TaskAssignmentUseCase;
import com.dxc.taskservice.application.port.out.TaskRepository;
import com.dxc.taskservice.application.port.out.feign.UserFeignPort;
import com.dxc.taskservice.application.security.TenantGuard;
import com.dxc.taskservice.domain.exception.RecordNotFoundException;
import com.dxc.taskservice.domain.model.aggregate.Task;
import com.dxc.taskservice.domain.model.entity.TaskAssignment;
import com.dxc.taskservice.domain.model.valueobject.TaskAssignmentStatus;
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
public class TaskAssignmentService implements TaskAssignmentUseCase {

    private final TaskRepository taskRepository;
    private final UserFeignPort userFeignPort;
    private final TenantGuard tenantGuard;
    private final TaskAssignmentApplicationMapper assignmentMapper;
    private final DomainEventPublisher eventPublisher;

    @Override
    public TaskAssignmentResponse assignUser(UUID taskId, AssignUserRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();

        // Verify the user to be assigned exists in tenant-service
        verifyUserExists(request.userId());

        Task task = loadTask(taskId, tenantId);
        task.assignUser(request.userId(), request.role(), request.allocationPercentage(), actionBy);
        taskRepository.save(task);
        eventPublisher.publish(task.getDomainEvents());
        task.clearDomainEvents();

        // Return the newly created assignment
        TaskAssignment created = task.getAssignments().stream()
                .filter(a -> a.getUserId().equals(request.userId())
                        && a.getRole() == request.role()
                        && a.getStatus() == TaskAssignmentStatus.ACTIVE)
                .reduce((first, second) -> second) // get the last match (most recently added)
                .orElseThrow(() -> new RecordNotFoundException("Assignment was not created"));

        return assignmentMapper.toResponse(created);
    }

    @Override
    public TaskAssignmentResponse updateAssignment(UUID taskId, UUID userId, UpdateAssignmentRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();

        Task task = loadTask(taskId, tenantId);
        task.updateAssignment(userId, request.role(), request.allocationPercentage(), actionBy);
        taskRepository.save(task);
        eventPublisher.publish(task.getDomainEvents());
        task.clearDomainEvents();

        TaskAssignment updated = task.getAssignments().stream()
                .filter(a -> a.getUserId().equals(userId) && a.getStatus() == TaskAssignmentStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Assignment not found"));

        return assignmentMapper.toResponse(updated);
    }

    @Override
    public void unassignUser(UUID taskId, UUID userId) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();

        Task task = loadTask(taskId, tenantId);
        task.unassignUser(userId, actionBy);
        taskRepository.save(task);
        eventPublisher.publish(task.getDomainEvents());
        task.clearDomainEvents();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskAssignmentResponse> getAssignments(UUID taskId) {
        UUID tenantId = getTenantIdAndVerify();
        Task task = loadTask(taskId, tenantId);
        return assignmentMapper.toResponseList(task.getAssignments());
    }

    private Task loadTask(UUID id, UUID tenantId) {
        return taskRepository.findById(id, tenantId)
                .orElseThrow(() -> new RecordNotFoundException("Task not found with id: " + id));
    }

    private void verifyUserExists(UUID userId) {
        userFeignPort.getUser(userId);
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
