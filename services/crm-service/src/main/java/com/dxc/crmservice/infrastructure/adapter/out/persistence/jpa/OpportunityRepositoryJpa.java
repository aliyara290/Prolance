package com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.OpportunityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpportunityRepositoryJpa extends JpaRepository<OpportunityEntity, UUID> {
    List<OpportunityEntity> findByTenantId(UUID tenantId);
    Optional<OpportunityEntity> findByIdAndTenantId(UUID id, UUID tenantId);
}
