package com.dxc.tenantservice.application.port.out;

import com.dxc.tenantservice.domain.model.tenant.TenantUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantUserRepository {
    TenantUser save(TenantUser user);
    Optional<TenantUser> findById(UUID id);
    Optional<TenantUser> findByKeycloakUserId(UUID keycloakUserId);
    Optional<TenantUser> findByEmailAndTenantId(String email, UUID tenantId);
    Page<TenantUser> findByTenantId(Pageable pageable, UUID tenantId);
    boolean existsByEmailAndTenantId(String email, UUID tenantId);
    long countByTenantId(UUID tenantId);
    void deleteById(UUID id);
}

