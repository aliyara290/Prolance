package com.dxc.crmservice.infrastructure.adapter.out.feign;

import com.dxc.crmservice.application.port.out.feign.TenantFeignPort;
import com.dxc.crmservice.domain.exception.RecordNotFoundException;
import com.dxc.crmservice.infrastructure.adapter.out.feign.client.TenantClient;
import com.dxc.crmservice.infrastructure.adapter.out.feign.dto.ResponseWrapper;
import com.dxc.crmservice.infrastructure.adapter.out.feign.dto.TenantStatusResponseDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantClientAdapter implements TenantFeignPort {
    private final TenantClient tenantClient;

    @Override
    @Cacheable(value = "tenant-status", key = "#p0")
    public boolean isTenantActive(UUID id) {
        try {
        log.info("Get tenant status");
        ResponseWrapper<TenantStatusResponseDTO> wrapper = tenantClient.getTenantStatus(id);
        TenantStatusResponseDTO response = wrapper.data();
        log.info("Tenant status 88888-----: {}", response);
        return response.active()
                && !response.deleted()
                && "ACTIVE".equals(response.status());
        } catch (FeignException.NotFound e) {
            throw new RecordNotFoundException("Tenant not found!");
        }
    }
}
