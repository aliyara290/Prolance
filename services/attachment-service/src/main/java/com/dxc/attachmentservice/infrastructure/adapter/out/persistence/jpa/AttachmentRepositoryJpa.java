package com.dxc.attachmentservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.attachmentservice.domain.model.valueobject.EntityType;
import com.dxc.attachmentservice.infrastructure.adapter.out.persistence.entity.AttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttachmentRepositoryJpa extends JpaRepository<AttachmentEntity, UUID> {

    Optional<AttachmentEntity> findByIdAndTenantIdAndDeletedFalse(UUID id, UUID tenantId);

    List<AttachmentEntity> findByEntityTypeAndEntityIdAndTenantIdAndDeletedFalse(
            EntityType entityType, UUID entityId, UUID tenantId);
}
