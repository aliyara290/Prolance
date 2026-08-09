package com.dxc.notificationservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.notificationservice.application.port.out.NotificationPreferenceRepository;
import com.dxc.notificationservice.domain.model.entity.NotificationPreference;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.entity.NotificationPreferenceEntity;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.jpa.NotificationPreferenceRepositoryJpa;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.mapper.NotificationPreferencePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationPreferenceRepositoryAdapter implements NotificationPreferenceRepository {

    private final NotificationPreferenceRepositoryJpa jpaRepository;
    private final NotificationPreferencePersistenceMapper mapper;

    @Override
    public NotificationPreference save(NotificationPreference preference) {
        NotificationPreferenceEntity entity = mapper.toEntity(preference);
        NotificationPreferenceEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<NotificationPreference> saveAll(List<NotificationPreference> preferences) {
        List<NotificationPreferenceEntity> entities = preferences.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        return jpaRepository.saveAll(entities).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationPreference> findByUserId(UUID tenantId, UUID userId) {
        return jpaRepository.findByTenantIdAndUserId(tenantId, userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<NotificationPreference> findByUserIdAndType(UUID tenantId, UUID userId, NotificationType type) {
        return jpaRepository.findByTenantIdAndUserIdAndNotificationType(tenantId, userId, type.name())
                .map(mapper::toDomain);
    }

    @Override
    public NotificationPreference getEffectivePreference(UUID tenantId, UUID userId, NotificationType type) {
        return findByUserIdAndType(tenantId, userId, type)
                .orElseGet(() -> NotificationPreference.builder()
                        .id(UUID.randomUUID())
                        .tenantId(tenantId)
                        .userId(userId)
                        .notificationType(type)
                        .inAppEnabled(true)
                        .emailEnabled(true)
                        .pushEnabled(false)
                        .smsEnabled(false)
                        .build());
    }
}
