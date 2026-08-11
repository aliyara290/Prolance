package com.dxc.billingservice.infrastructure.adapter.out.feign;

import com.dxc.billingservice.infrastructure.adapter.in.rest.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "tenant-service", path = "/api/v1/tenants")
public interface TenantFeignClient {

    @GetMapping("/users/{userId}")
    ApiResponse<UserResponseDTO> getUser(@PathVariable("userId") UUID userId);

    record UserResponseDTO(
            UUID id,
            UUID tenantId,
            String email,
            String firstName,
            String lastName,
            String seniorityLevel,
            String educationLevel,
            java.math.BigDecimal baseHourlySalary
    ) {
    }
}
