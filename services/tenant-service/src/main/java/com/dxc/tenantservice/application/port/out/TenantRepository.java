package com.dxc.tenantservice.application.port.out;

import com.dxc.tenantservice.domain.model.tenant.Tenant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {
    Tenant save(Tenant tenant);
    Optional<Tenant> findById(UUID id);
    Optional<Tenant> findByEmail(String email);
    List<Tenant> findAll();
    boolean existsByEmail(String email);
    void deleteById(UUID id);
}
