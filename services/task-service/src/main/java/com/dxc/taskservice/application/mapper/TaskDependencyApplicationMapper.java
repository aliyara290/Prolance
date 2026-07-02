package com.dxc.taskservice.application.mapper;

import com.dxc.taskservice.application.dto.dependency.res.TaskDependencyResponse;
import com.dxc.taskservice.domain.model.entity.TaskDependency;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskDependencyApplicationMapper {

    public TaskDependencyResponse toResponse(TaskDependency dependency) {
        if (dependency == null) return null;

        return new TaskDependencyResponse(
            dependency.getId(),
            dependency.getTaskId(),
            dependency.getDependOnTaskId(),
            dependency.getType(),
            dependency.getCreatedAt()
        );
    }

    public List<TaskDependencyResponse> toResponseList(List<TaskDependency> dependencies) {
        if (dependencies == null) return null;
        return dependencies.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
