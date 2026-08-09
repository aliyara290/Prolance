package com.dxc.notificationservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.notificationservice.domain.model.aggregate.Notification;
import com.dxc.notificationservice.domain.model.valueobject.NotificationCategory;
import com.dxc.notificationservice.domain.model.valueobject.NotificationPriority;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.domain.model.valueobject.ReadStatus;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationPersistenceMapper {

    public NotificationEntity toEntity(Notification domain) {
        if (domain == null) return null;

        return NotificationEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .recipientId(domain.getRecipientId())
                .senderId(domain.getSenderId())
                .title(domain.getTitle())
                .message(domain.getMessage())
                .type(domain.getType().name())
                .priority(domain.getPriority().name())
                .category(domain.getCategory().name())
                .icon(domain.getIcon())
                .entityType(domain.getEntityType())
                .entityId(domain.getEntityId())
                .actionUrl(domain.getActionUrl())
                .readStatus(domain.getReadStatus().name())
                .readAt(domain.getReadAt())
                .archivedAt(domain.getArchivedAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public Notification toDomain(NotificationEntity entity) {
        if (entity == null) return null;

        return Notification.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .recipientId(entity.getRecipientId())
                .senderId(entity.getSenderId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .type(NotificationType.valueOf(entity.getType()))
                .priority(NotificationPriority.valueOf(entity.getPriority()))
                .category(NotificationCategory.valueOf(entity.getCategory()))
                .icon(entity.getIcon())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .actionUrl(entity.getActionUrl())
                .readStatus(ReadStatus.valueOf(entity.getReadStatus()))
                .readAt(entity.getReadAt())
                .archivedAt(entity.getArchivedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
