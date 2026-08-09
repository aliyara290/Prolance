package com.dxc.attachmentservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.attachmentservice.application.port.out.AttachmentRepository;
import com.dxc.attachmentservice.domain.model.entity.Attachment;
import com.dxc.attachmentservice.domain.model.valueobject.EntityType;
import com.dxc.attachmentservice.infrastructure.adapter.out.persistence.entity.AttachmentEntity;
import com.dxc.attachmentservice.infrastructure.adapter.out.persistence.jpa.AttachmentRepositoryJpa;
import com.dxc.attachmentservice.infrastructure.adapter.out.persistence.mapper.AttachmentPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AttachmentRepositoryAdapter implements AttachmentRepository {

    private final AttachmentRepositoryJpa repository;
    private final AttachmentPersistenceMapper mapper;

    @Override
    public Attachment save(Attachment domain) {
        AttachmentEntity entity = mapper.toEntity(domain);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Attachment> findById(UUID id, UUID tenantId) {
        return repository.findByIdAndTenantIdAndDeletedFalse(id, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Attachment> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId, UUID tenantId) {
        return repository.findByEntityTypeAndEntityIdAndTenantIdAndDeletedFalse(entityType, entityId, tenantId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        repository.findByIdAndTenantIdAndDeletedFalse(id, tenantId)
                .ifPresent(entity -> {
                    entity.setDeleted(true);
                    repository.save(entity);
                });
    }
}
