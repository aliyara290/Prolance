package com.dxc.projectservice.infrastructure.adapter.out.feign.client;

import com.dxc.projectservice.infrastructure.adapter.out.feign.config.FeignConfig;
import com.dxc.projectservice.infrastructure.adapter.out.feign.dto.ResponseWrapper;
import com.dxc.projectservice.infrastructure.adapter.out.feign.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
        name = "userClient",
        url = "${services.tenant-service.url}tenants/users",
        configuration = FeignConfig.class
)
public interface UserClient {

    @GetMapping("/{id}")
    ResponseEntity<ResponseWrapper<UserResponseDTO>> getUser(@RequestBody @PathVariable("id") UUID id);
}