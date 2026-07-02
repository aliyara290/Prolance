package com.dxc.taskservice.application.mapper;

import com.dxc.taskservice.application.dto.assignment.res.TaskAssignmentResponse;
import com.dxc.taskservice.domain.model.entity.TaskAssignment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskAssignmentApplicationMapper {

    public TaskAssignmentResponse toResponse(TaskAssignment assignment) {
        if (assignment == null) return null;

        return new TaskAssignmentResponse(
            assignment.getId(),
            assignment.getTaskId(),
            assignment.getUserId(),
            assignment.getRole(),
            assignment.getAllocationPercentage(),
            assignment.getStatus(),
            assignment.getAssignedAt(),
            assignment.getUnassignedAt()
        );
    }

    public List<TaskAssignmentResponse> toResponseList(List<TaskAssignment> assignments) {
        if (assignments == null) return null;
        return assignments.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
