package com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityRepositoryJpa extends JpaRepository<ActivityEntity, UUID> {
    List<ActivityEntity> findByTenantId(UUID tenantId);
    Optional<ActivityEntity> findByIdAndTenantId(UUID id, UUID tenantId);
}
