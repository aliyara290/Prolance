package com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepositoryJpa extends JpaRepository<ClientEntity, UUID> {
    Page<ClientEntity> findByTenantId(UUID tenantId, Pageable pageable);
    Optional<ClientEntity> findByIdAndTenantId(UUID id, UUID tenantId);
}
