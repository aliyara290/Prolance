package com.dxc.taskservice.infrastructure.adapter.out.feign.client;

import com.dxc.taskservice.infrastructure.adapter.out.feign.config.FeignConfig;
import com.dxc.taskservice.infrastructure.adapter.out.feign.dto.ResponseWrapper;
import com.dxc.taskservice.infrastructure.adapter.out.feign.dto.TenantStatusResponseDTO;
import com.dxc.taskservice.infrastructure.adapter.out.feign.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
        name = "tenant-service",
        configuration = FeignConfig.class
)
public interface TenantServiceClient {

    @GetMapping("/api/v1/tenants/{id}/status")
    ResponseWrapper<TenantStatusResponseDTO> getTenantStatus(@PathVariable("id") UUID id);

    @GetMapping("/api/v1/tenants/users/{id}/keycloak")
    ResponseEntity<ResponseWrapper<UserResponseDTO>> getUser(@RequestBody @PathVariable("id") UUID id);
}
