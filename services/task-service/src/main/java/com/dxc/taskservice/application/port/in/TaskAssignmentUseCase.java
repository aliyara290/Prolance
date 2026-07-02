package com.dxc.taskservice.application.port.in;

import com.dxc.taskservice.application.dto.assignment.req.AssignUserRequest;
import com.dxc.taskservice.application.dto.assignment.req.UpdateAssignmentRequest;
import com.dxc.taskservice.application.dto.assignment.res.TaskAssignmentResponse;

import java.util.List;
import java.util.UUID;

public interface TaskAssignmentUseCase {
    TaskAssignmentResponse assignUser(UUID taskId, AssignUserRequest request);
    TaskAssignmentResponse updateAssignment(UUID taskId, UUID userId, UpdateAssignmentRequest request);
    void unassignUser(UUID taskId, UUID userId);
    List<TaskAssignmentResponse> getAssignments(UUID taskId);
}
