package com.dxc.tenantservice.application.service;

import com.dxc.tenantservice.application.dto.tenant.req.RegisterTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.req.UpdateTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantResDTO;
import com.dxc.tenantservice.application.mapper.TenantDtoMapper;
import com.dxc.tenantservice.application.mapper.TenantUserDtoMapper;
import com.dxc.tenantservice.application.port.in.TenantUseCase;
import com.dxc.tenantservice.application.port.out.TenantLogRepository;
import com.dxc.tenantservice.application.port.out.TenantRepository;
import com.dxc.tenantservice.application.port.out.TenantUserRepository;
import com.dxc.tenantservice.application.saga.RegistrationSaga;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.client.KeycloakUserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class TenantService implements TenantUseCase {

    private final TenantRepository tenantRepository;
    private final TenantUserRepository tenantUserRepository;
    private final TenantLogRepository tenantLogRepository;
    private final TenantDtoMapper tenantDtoMapper;
    private final TenantUserDtoMapper tenantUserDtoMapper;
    private final ApplicationContext ctx;

    @Override
    public TenantResDTO registerTenant(RegisterTenantReqDTO registerTenantReqDTO) {
        RegistrationSaga saga = ctx.getBean(RegistrationSaga.class);
        UUID tenantId = UUID.randomUUID();
        UUID keycloakUserId = null;

        try{

        } catch (Exception ex) {

        }
        return null;
    }

    @Override
    public TenantResDTO updateTenant(UUID tenantId, UpdateTenantReqDTO updateTenantReqDTO) {
        return null;
    }

    @Override
    public TenantResDTO deleteTenant(UUID tenantId) {
        return null;
    }

    @Override
    public TenantResDTO getTenant(UUID tenantId) {
        return null;
    }

    @Override
    public List<TenantResDTO> getAllTenants() {
        return List.of();
    }
}
