package com.dxc.taskservice.application.port.in;

import com.dxc.taskservice.application.dto.task.req.ChangeTaskStatusRequest;
import com.dxc.taskservice.application.dto.task.req.CreateTaskRequest;
import com.dxc.taskservice.application.dto.task.req.UpdateTaskRequest;
import com.dxc.taskservice.application.dto.task.res.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskUseCase {
    TaskResponse createTask(CreateTaskRequest request);
    TaskResponse updateTask(UUID id, UpdateTaskRequest request);
    void deleteTask(UUID id);
    TaskResponse getTask(UUID id);
    TaskResponse changeTaskStatus(UUID id, ChangeTaskStatusRequest request);
    Page<TaskResponse> getTasks(Pageable pageable);
    Page<TaskResponse> getTasksByProjectId(UUID projectId, Pageable pageable);
    Page<TaskResponse> getTasksByMilestoneId(UUID milestoneId, Pageable pageable);
    Page<TaskResponse> getTasksByAssigneeId(UUID assigneeId, Pageable pageable);
}
