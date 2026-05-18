package com.dxc.crmservice.infrastructure.adapter.out.feign.client;

import com.dxc.crmservice.infrastructure.adapter.out.feign.config.FeignConfig;
import com.dxc.crmservice.infrastructure.adapter.out.feign.dto.ResponseWrapper;
import com.dxc.crmservice.infrastructure.adapter.out.feign.dto.TenantStatusResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "tenantClient",
        url = "${services.tenant-service.url}tenants",
        configuration = FeignConfig.class
)
public interface TenantClient {

    @GetMapping("/{id}/status")
    ResponseWrapper<TenantStatusResponseDTO> getTenantStatus(@PathVariable("id") UUID id);
}
