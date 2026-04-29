package com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.LeadEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeadRepositoryJpa extends JpaRepository<LeadEntity, UUID> {
    Page<LeadEntity> findByTenantId(UUID tenantId, Pageable pageable);
    Optional<LeadEntity> findByIdAndTenantId(UUID id, UUID tenantId);
}
