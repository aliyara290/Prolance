package com.dxc.attachmentservice.application.port.out;

import com.dxc.attachmentservice.domain.model.entity.Attachment;
import com.dxc.attachmentservice.domain.model.valueobject.EntityType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository {

    Attachment save(Attachment attachment);

    Optional<Attachment> findById(UUID id, UUID tenantId);

    List<Attachment> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId, UUID tenantId);

    void delete(UUID id, UUID tenantId);
}
