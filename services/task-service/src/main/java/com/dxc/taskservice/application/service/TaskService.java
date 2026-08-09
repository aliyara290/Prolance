package com.dxc.taskservice.application.service;

import com.dxc.taskservice.application.dto.assignment.req.AssignUserRequest;
import com.dxc.taskservice.application.dto.attachment.req.AddAttachmentRequest;
import com.dxc.taskservice.application.dto.dependency.req.AddDependencyRequest;
import com.dxc.taskservice.application.dto.task.req.ChangeTaskStatusRequest;
import com.dxc.taskservice.application.dto.task.req.CreateTaskRequest;
import com.dxc.taskservice.application.dto.task.req.UpdateTaskRequest;
import com.dxc.taskservice.application.dto.task.res.TaskResponse;
import com.dxc.taskservice.application.mapper.TaskApplicationMapper;
import com.dxc.taskservice.application.port.in.TaskUseCase;
import com.dxc.taskservice.application.port.out.TaskRepository;
import com.dxc.taskservice.application.port.out.feign.ProjectFeignPort;
import com.dxc.taskservice.application.port.out.feign.UserFeignPort;
import com.dxc.taskservice.application.security.TenantGuard;
import com.dxc.taskservice.application.port.out.DomainEventPublisher;
import com.dxc.taskservice.domain.exception.RecordNotFoundException;
import com.dxc.taskservice.domain.model.aggregate.Task;
import com.dxc.taskservice.infrastructure.adapter.out.feign.dto.ProjectValidationResponse;
import com.dxc.taskservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class TaskService implements TaskUseCase {
    private final TaskRepository taskRepository;
    private final TaskApplicationMapper taskApplicationMapper;
    private final ProjectFeignPort projectFeignPort;
    private final UserFeignPort userFeignPort;
    private final TenantGuard tenantGuard;
    private final DomainEventPublisher eventPublisher;

    @Override
    public TaskResponse createTask(CreateTaskRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID userId = TenantContextHolder.getUserId();
        ProjectValidationResponse response = projectFeignPort.canCreateTaskInProject(request.projectId(), userId);
        if (!response.allowed()) {
            throw new IllegalArgumentException(response.message());
        }

        if (request.reporterId() != null && !request.reporterId().equals(userId)) {
            verifyUserExists(request.reporterId());
        }

        Task task = Task.create(
                tenantId,
                request.projectId(),
                request.milestoneId() != null ? request.milestoneId() : null,
                request.title(),
                request.description(),
                request.type(),
                request.status(),
                request.priority(),
                request.startDate(),
                request.dueDate(),
                userId,
                request.reporterId() != null ? request.reporterId() : userId
        );

        if(request.assignments() != null) {
            for (AssignUserRequest assignment : request.assignments()) {
                verifyUserExists(assignment.userId());
                task.assignUser(assignment.userId(), assignment.role(), assignment.allocationPercentage(), userId);
            }
        }

        if(request.dependencies() != null) {
            for (AddDependencyRequest dependency : request.dependencies()) {
                verifyTaskExists(dependency.dependOnTaskId(), tenantId);
                task.addDependency(dependency.dependOnTaskId(), dependency.type(), userId);
            }
        }

        if(request.attachments() != null) {
            for (AddAttachmentRequest attachment : request.attachments()) {
                task.addAttachment(attachment.fileUrl(), attachment.fileName(), attachment.fileType(), userId, userId);
            }
        }

        taskRepository.save(task);
        eventPublisher.publish(task.getDomainEvents());
        task.clearDomainEvents();

        return taskApplicationMapper.toResponse(task);
    }

    @Override
    public TaskResponse updateTask(UUID id, UpdateTaskRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID userId = TenantContextHolder.getUserId();

        Task task = loadTask(id, tenantId);

        task.updateTask(
                request.title(),
                request.description(),
                request.type(),
                request.priority(),
                request.startDate(),
                request.dueDate(),
                request.milestoneId(),
                userId
        );

        taskRepository.save(task);
        eventPublisher.publish(task.getDomainEvents());
        task.clearDomainEvents();
        return taskApplicationMapper.toResponse(task);
    }

    @Override
    public void deleteTask(UUID id) {
        UUID tenantId = getTenantIdAndVerify();
        UUID userId = TenantContextHolder.getUserId();

        Task task = loadTask(id, tenantId);
        task.deleteTask(userId);

        taskRepository.delete(id, tenantId);
        eventPublisher.publish(task.getDomainEvents());
        task.clearDomainEvents();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTask(UUID id) {
        UUID tenantId = getTenantIdAndVerify();
        Task task = loadTask(id, tenantId);
        return taskApplicationMapper.toResponse(task);
    }

    @Override
    public TaskResponse changeTaskStatus(UUID id, ChangeTaskStatusRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID userId = TenantContextHolder.getUserId();

        Task task = loadTask(id, tenantId);
        task.changeStatus(request.newStatus(), userId, request.comment());

        taskRepository.save(task);
        eventPublisher.publish(task.getDomainEvents());
        task.clearDomainEvents();
        return taskApplicationMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasks(Pageable pageable) {
        UUID tenantId = getTenantIdAndVerify();
        return taskRepository.findAll(tenantId, pageable)
                .map(taskApplicationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByProjectId(UUID projectId, Pageable pageable) {
        UUID tenantId = getTenantIdAndVerify();
        return taskRepository.findByProjectId(projectId, tenantId, pageable)
                .map(taskApplicationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByMilestoneId(UUID milestoneId, Pageable pageable) {
        UUID tenantId = getTenantIdAndVerify();
        return taskRepository.findByMilestoneId(milestoneId, tenantId, pageable)
                .map(taskApplicationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByAssigneeId(UUID assigneeId, Pageable pageable) {
        UUID tenantId = getTenantIdAndVerify();
        return taskRepository.findByAssigneeId(assigneeId, tenantId, pageable)
                .map(taskApplicationMapper::toResponse);
    }

    private Task loadTask(UUID id, UUID tenantId) {
        return taskRepository.findById(id, tenantId)
                .orElseThrow(() -> new RecordNotFoundException("Task not found with id: " + id));
    }

    private void verifyUserExists(UUID userId) {
        userFeignPort.getUser(userId);
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
