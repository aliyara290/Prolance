package com.dxc.taskservice.infrastructure.adapter.out.feign.client;

import com.dxc.taskservice.infrastructure.adapter.out.feign.config.FeignConfig;
import com.dxc.taskservice.infrastructure.adapter.out.feign.dto.CanCreateTaskRequest;
import com.dxc.taskservice.infrastructure.adapter.out.feign.dto.ProjectValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
        name = "projectClient",
        url = "${services.project-service.url}projects",
        configuration = FeignConfig.class
)
public interface ProjectClient {
    @PostMapping("/{projectId}/task-creation-validation")
    ProjectValidationResponse canCreateTaskInProject(@PathVariable("projectId") UUID projectId, @RequestBody CanCreateTaskRequest request);
//    ProjectValidationResponse isProjectActive(UUID projectId);
//    ProjectValidationResponse isUserMemberInProject(UUID projectId, UUID userId);
//    ProjectValidationResponse isUserHavePermissionInProject(UUID projectId, UUID userId);
}
