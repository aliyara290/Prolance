package com.dxc.notificationservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.notificationservice.domain.model.entity.NotificationPreference;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.entity.NotificationPreferenceEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationPreferencePersistenceMapper {

    public NotificationPreferenceEntity toEntity(NotificationPreference domain) {
        if (domain == null) return null;

        NotificationPreferenceEntity entity = new NotificationPreferenceEntity();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setUserId(domain.getUserId());
        entity.setNotificationType(domain.getNotificationType().name());
        entity.setInAppEnabled(domain.isInAppEnabled());
        entity.setEmailEnabled(domain.isEmailEnabled());
        entity.setPushEnabled(domain.isPushEnabled());
        entity.setSmsEnabled(domain.isSmsEnabled());

        return entity;
    }

    public NotificationPreference toDomain(NotificationPreferenceEntity entity) {
        if (entity == null) return null;

        return NotificationPreference.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .userId(entity.getUserId())
                .notificationType(NotificationType.valueOf(entity.getNotificationType()))
                .inAppEnabled(entity.isInAppEnabled())
                .emailEnabled(entity.isEmailEnabled())
                .pushEnabled(entity.isPushEnabled())
                .smsEnabled(entity.isSmsEnabled())
                .build();
    }
}
