package com.dxc.notificationservice.infrastructure.adapter.out.feign.client;

import com.dxc.notificationservice.infrastructure.adapter.out.feign.dto.ResponseWrapper;
import com.dxc.notificationservice.infrastructure.adapter.out.feign.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "tenant-service", url = "${feign.client.tenant-service.url:http://localhost:8081}")
public interface UserFeignClient {

    @GetMapping("/api/v1/users/{id}")
    ResponseEntity<ResponseWrapper<UserResponseDTO>> getUser(@PathVariable("id") UUID id);
}
