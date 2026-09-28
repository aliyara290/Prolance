package com.dxc.billingservice.infrastructure.adapter.out.feign;

import com.dxc.billingservice.application.port.out.feign.TenantFeignPort;
import com.dxc.billingservice.domain.model.valueobject.EducationLevel;
import com.dxc.billingservice.domain.model.valueobject.SeniorityLevel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TenantFeignAdapter implements TenantFeignPort {

    private static final Logger log = LoggerFactory.getLogger(TenantFeignAdapter.class);
    
    private final TenantFeignClient tenantFeignClient;

    @Override
    public TenantUserDTO getUser(UUID userId) {
        log.info("Fetching user {} from tenant-service", userId);
        var response = tenantFeignClient.getUser(userId);
        if (response != null && response.isSuccess() && response.getData() != null) {
            var data = response.getData();
            return TenantUserDTO.builder()
                    .id(data.id())
                    .tenantId(data.tenantId())
                    .email(data.email())
                    .firstName(data.firstName())
                    .lastName(data.lastName())
                    .seniorityLevel(data.seniorityLevel() != null ? SeniorityLevel.valueOf(data.seniorityLevel()) : null)
                    .educationLevel(data.educationLevel() != null ? EducationLevel.valueOf(data.educationLevel()) : null)
                    .baseHourlySalary(data.baseHourlySalary())
                    .build();
        }
        return null;
    }
}
