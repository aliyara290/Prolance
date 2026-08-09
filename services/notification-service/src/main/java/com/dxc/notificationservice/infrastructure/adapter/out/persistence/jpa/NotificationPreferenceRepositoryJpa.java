package com.dxc.notificationservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.notificationservice.infrastructure.adapter.out.persistence.entity.NotificationPreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationPreferenceRepositoryJpa extends JpaRepository<NotificationPreferenceEntity, UUID> {
    List<NotificationPreferenceEntity> findByTenantIdAndUserId(UUID tenantId, UUID userId);
    Optional<NotificationPreferenceEntity> findByTenantIdAndUserIdAndNotificationType(UUID tenantId, UUID userId, String notificationType);
}
