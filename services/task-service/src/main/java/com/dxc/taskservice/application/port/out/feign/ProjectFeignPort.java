package com.dxc.taskservice.application.port.out.feign;

import com.dxc.taskservice.infrastructure.adapter.out.feign.dto.ProjectValidationResponse;

import java.util.UUID;

public interface ProjectFeignPort {
    ProjectValidationResponse canCreateTaskInProject(UUID projectId, UUID userId);
    ProjectValidationResponse isProjectActive(UUID projectId);
    ProjectValidationResponse isUserMemberInProject(UUID projectId, UUID userId);
    ProjectValidationResponse isUserHavePermissionInProject(UUID projectId, UUID userId);
}

