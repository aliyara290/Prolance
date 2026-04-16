package com.dxc.tenantservice.application.service;

import com.dxc.tenantservice.application.dto.tenant.req.RegisterTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.req.UpdateTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantResDTO;
import com.dxc.tenantservice.application.port.in.TenantUseCase;
import com.dxc.tenantservice.application.port.out.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class TenantService implements TenantUseCase {

    private final TenantRepository tenantRepositoryPort;

    @Override
    public TenantResDTO registerTenant(RegisterTenantReqDTO registerTenantReqDTO) {
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
