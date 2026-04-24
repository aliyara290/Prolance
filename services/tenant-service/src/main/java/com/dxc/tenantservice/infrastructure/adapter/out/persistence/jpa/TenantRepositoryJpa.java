package com.dxc.tenantservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepositoryJpa extends JpaRepository<TenantEntity, UUID> {
    Optional<TenantEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
