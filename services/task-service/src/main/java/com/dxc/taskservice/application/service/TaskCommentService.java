package com.dxc.taskservice.application.service;

import com.dxc.taskservice.application.dto.comment.req.AddCommentRequest;
import com.dxc.taskservice.application.dto.comment.req.EditCommentRequest;
import com.dxc.taskservice.application.dto.comment.res.TaskCommentResponse;
import com.dxc.taskservice.application.mapper.TaskCommentApplicationMapper;
import com.dxc.taskservice.application.port.in.TaskCommentUseCase;
import com.dxc.taskservice.application.port.out.TaskRepository;
import com.dxc.taskservice.application.port.out.feign.UserFeignPort;
import com.dxc.taskservice.application.security.TenantGuard;
import com.dxc.taskservice.domain.exception.RecordNotFoundException;
import com.dxc.taskservice.domain.model.aggregate.Task;
import com.dxc.taskservice.domain.model.entity.TaskComment;
import com.dxc.taskservice.infrastructure.config.TenantContextHolder;
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
public class TaskCommentService implements TaskCommentUseCase {

    private final TaskRepository taskRepository;
    private final UserFeignPort userFeignPort;
    private final TenantGuard tenantGuard;
    private final TaskCommentApplicationMapper commentMapper;

    @Override
    public TaskCommentResponse addComment(UUID taskId, AddCommentRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID userId = TenantContextHolder.getUserId();

        // Verify the commenting user exists in tenant-service
        verifyUserExists(userId);

        Task task = loadTask(taskId, tenantId);
        task.addComment(userId, request.content(), userId);
        taskRepository.save(task);

        // Return the newly created comment
        TaskComment created = task.getComments().get(task.getComments().size() - 1);
        return commentMapper.toResponse(created);
    }

    @Override
    public TaskCommentResponse editComment(UUID taskId, UUID commentId, EditCommentRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID userId = TenantContextHolder.getUserId();

        Task task = loadTask(taskId, tenantId);
        task.editComment(commentId, request.content(), userId, userId);
        taskRepository.save(task);

        TaskComment edited = task.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Comment not found with id: " + commentId));

        return commentMapper.toResponse(edited);
    }

    @Override
    public void removeComment(UUID taskId, UUID commentId) {
        UUID tenantId = getTenantIdAndVerify();
        UUID userId = TenantContextHolder.getUserId();

        Task task = loadTask(taskId, tenantId);
        task.removeComment(commentId, userId);
        taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskCommentResponse> getComments(UUID taskId) {
        UUID tenantId = getTenantIdAndVerify();
        Task task = loadTask(taskId, tenantId);
        return commentMapper.toResponseList(task.getComments());
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
