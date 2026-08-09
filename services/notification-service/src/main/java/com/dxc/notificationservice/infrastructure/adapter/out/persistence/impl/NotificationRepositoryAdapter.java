package com.dxc.notificationservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.notificationservice.application.port.out.NotificationRepository;
import com.dxc.notificationservice.domain.model.aggregate.Notification;
import com.dxc.notificationservice.domain.model.valueobject.NotificationCategory;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.domain.model.valueobject.ReadStatus;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.entity.NotificationEntity;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.jpa.NotificationRepositoryJpa;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.mapper.NotificationPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationRepositoryJpa jpaRepository;
    private final NotificationPersistenceMapper mapper;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = mapper.toEntity(notification);
        NotificationEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(UUID id, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        jpaRepository.findByIdAndTenantId(id, tenantId)
                .ifPresent(jpaRepository::delete);
    }

    @Override
    public Page<Notification> findByFilters(UUID tenantId, UUID recipientId, NotificationType type, ReadStatus readStatus, NotificationCategory category, Pageable pageable) {
        String typeStr = type != null ? type.name() : null;
        String statusStr = readStatus != null ? readStatus.name() : null;
        String catStr = category != null ? category.name() : null;

        return jpaRepository.findByFilters(tenantId, recipientId, typeStr, statusStr, catStr, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public long countUnread(UUID tenantId, UUID recipientId) {
        return jpaRepository.countUnread(tenantId, recipientId);
    }

    @Override
    public void markAllAsRead(UUID tenantId, UUID recipientId) {
        jpaRepository.markAllAsRead(tenantId, recipientId);
    }
}
