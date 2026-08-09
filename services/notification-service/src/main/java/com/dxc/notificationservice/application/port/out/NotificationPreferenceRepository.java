package com.dxc.notificationservice.application.port.out;

import com.dxc.notificationservice.domain.model.entity.NotificationPreference;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferenceRepository {
    NotificationPreference save(NotificationPreference preference);
    List<NotificationPreference> saveAll(List<NotificationPreference> preferences);
    List<NotificationPreference> findByUserId(UUID tenantId, UUID userId);
    Optional<NotificationPreference> findByUserIdAndType(UUID tenantId, UUID userId, NotificationType type);
    
    /**
     * Finds a preference or returns a default implementation if not explicitly set
     */
    NotificationPreference getEffectivePreference(UUID tenantId, UUID userId, NotificationType type);
}
