package com.dxc.tenantservice.application.port.out;

import com.dxc.tenantservice.domain.model.tenant.TenantLog;

import java.util.List;
import java.util.UUID;

public interface TenantLogRepository {
    void save(TenantLog log);
    List<TenantLog> findAllByTenantId(UUID tenantId);
}