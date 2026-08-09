package com.dxc.notificationservice.application.port.out;

import com.dxc.notificationservice.domain.model.aggregate.Notification;
import com.dxc.notificationservice.domain.model.valueobject.NotificationCategory;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.domain.model.valueobject.ReadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(UUID id, UUID tenantId);
    void delete(UUID id, UUID tenantId);
    
    Page<Notification> findByFilters(
            UUID tenantId,
            UUID recipientId,
            NotificationType type,
            ReadStatus readStatus,
            NotificationCategory category,
            Pageable pageable
    );
    
    long countUnread(UUID tenantId, UUID recipientId);
    
    void markAllAsRead(UUID tenantId, UUID recipientId);
}
