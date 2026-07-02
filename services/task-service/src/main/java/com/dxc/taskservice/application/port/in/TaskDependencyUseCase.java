package com.dxc.taskservice.application.port.in;

import com.dxc.taskservice.application.dto.dependency.req.AddDependencyRequest;
import com.dxc.taskservice.application.dto.dependency.res.TaskDependencyResponse;

import java.util.List;
import java.util.UUID;

public interface TaskDependencyUseCase {
    TaskDependencyResponse addDependency(UUID taskId, AddDependencyRequest request);
    void removeDependency(UUID taskId, UUID dependencyId);
    List<TaskDependencyResponse> getDependencies(UUID taskId);
}
