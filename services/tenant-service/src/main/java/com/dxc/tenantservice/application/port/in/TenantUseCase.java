package com.dxc.tenantservice.application.port.in;

import com.dxc.tenantservice.application.dto.tenant.req.RegisterTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.req.UpdateTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantRegistrationResponseDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantResDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantStatusRespDTO;

import java.util.UUID;

public interface TenantUseCase {
    TenantRegistrationResponseDTO registerTenant(RegisterTenantReqDTO registerTenantReqDTO);
    TenantResDTO updateTenant(UUID tenantId, UpdateTenantReqDTO updateTenantReqDTO);
    void deleteTenant(UUID tenantId);
    TenantResDTO getTenant(UUID tenantId);
    TenantStatusRespDTO getTenantStatus(UUID tenantId);
}

