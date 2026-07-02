package com.dxc.taskservice.infrastructure.adapter.out.feign;

import com.dxc.taskservice.application.port.out.feign.ProjectFeignPort;
import com.dxc.taskservice.infrastructure.adapter.out.feign.client.ProjectClient;
import com.dxc.taskservice.infrastructure.adapter.out.feign.dto.CanCreateTaskRequest;
import com.dxc.taskservice.infrastructure.adapter.out.feign.dto.ProjectValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectClientAdapter implements ProjectFeignPort {
    private final ProjectClient projectClient;

    @Override
    public ProjectValidationResponse canCreateTaskInProject(UUID projectId, UUID userId) {
        return projectClient.canCreateTaskInProject(projectId, new CanCreateTaskRequest(userId));
    }

    @Override
    public ProjectValidationResponse isProjectActive(UUID projectId) {
        return null;
    }

    @Override
    public ProjectValidationResponse isUserMemberInProject(UUID projectId, UUID userId) {
        return null;
    }

    @Override
    public ProjectValidationResponse isUserHavePermissionInProject(UUID projectId, UUID userId) {
        return null;
    }
}
