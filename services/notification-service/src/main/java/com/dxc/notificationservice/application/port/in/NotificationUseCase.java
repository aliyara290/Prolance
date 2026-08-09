package com.dxc.notificationservice.application.port.in;

import com.dxc.notificationservice.application.dto.notification.res.NotificationResponse;
import com.dxc.notificationservice.domain.model.valueobject.NotificationCategory;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.domain.model.valueobject.ReadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationUseCase {
    Page<NotificationResponse> getNotifications(
            UUID recipientId,
            NotificationType type,
            ReadStatus readStatus,
            NotificationCategory category,
            Pageable pageable
    );

    long getUnreadCount(UUID recipientId);

    void markAsRead(UUID id, UUID recipientId);

    void markAllAsRead(UUID recipientId);

    void archive(UUID id, UUID recipientId);

    void delete(UUID id, UUID recipientId);
}
