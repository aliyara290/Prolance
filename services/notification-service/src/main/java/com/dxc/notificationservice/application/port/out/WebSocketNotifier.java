package com.dxc.notificationservice.application.port.out;

import com.dxc.notificationservice.application.dto.notification.res.NotificationResponse;
import java.util.UUID;

public interface WebSocketNotifier {
    void pushNewNotification(UUID userId, NotificationResponse notification, long unreadCount);
    void pushUnreadCount(UUID userId, long unreadCount);
    void pushNotificationRead(UUID userId, UUID notificationId, long unreadCount);
}
