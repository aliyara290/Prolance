package com.dxc.notificationservice.infrastructure.adapter.out.feign.client;

import com.dxc.notificationservice.infrastructure.adapter.out.feign.dto.ProjectResponseDTO;
import com.dxc.notificationservice.infrastructure.adapter.out.feign.dto.ResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "project-service", url = "${feign.client.project-service.url:http://localhost:8083}")
public interface ProjectFeignClient {

    @GetMapping("/api/v1/projects/{id}")
    ResponseEntity<ResponseWrapper<ProjectResponseDTO>> getProject(@PathVariable("id") UUID id);
}
