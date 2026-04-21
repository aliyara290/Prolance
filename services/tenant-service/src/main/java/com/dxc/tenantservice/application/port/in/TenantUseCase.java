package com.dxc.tenantservice.application.port.in;

import com.dxc.tenantservice.application.dto.tenant.req.RegisterTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.req.UpdateTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantResDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantTokenResDTO;

import java.util.List;
import java.util.UUID;

public interface TenantUseCase {
    TenantTokenResDTO registerTenant(RegisterTenantReqDTO registerTenantReqDTO);
    TenantResDTO updateTenant(UUID tenantId, UpdateTenantReqDTO updateTenantReqDTO);
    void deleteTenant(UUID tenantId);
    TenantResDTO getTenant(UUID tenantId);
}

