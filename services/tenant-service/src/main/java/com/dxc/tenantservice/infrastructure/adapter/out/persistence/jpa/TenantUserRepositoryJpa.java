package com.dxc.tenantservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.TenantUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantUserRepositoryJpa extends JpaRepository<TenantUserEntity, UUID> {
    Optional<TenantUserEntity> findByKeycloakUserId(UUID keycloakUserId);
    Optional<TenantUserEntity> findByKeycloakUserIdAndTenantId(UUID keycloakUserId, UUID tenantId);
    Optional<TenantUserEntity> findByEmailAndTenantId(String email, UUID tenantId);
    Page<TenantUserEntity> findByTenantId(Pageable pageable, UUID tenantId);
    boolean existsByEmailAndTenantId(String email, UUID tenantId);
    long countByTenantId(UUID tenantId);
}
