package com.dxc.notificationservice.infrastructure.adapter.out.websocket;

import com.dxc.notificationservice.application.dto.notification.res.NotificationResponse;
import com.dxc.notificationservice.application.port.out.WebSocketNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompWebSocketNotifier implements WebSocketNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void pushNewNotification(UUID userId, NotificationResponse notification, long unreadCount) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "NEW_NOTIFICATION");
        payload.put("notification", notification);
        payload.put("unreadCount", unreadCount);

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                payload
        );
        log.debug("Pushed new notification via WebSocket to user {}", userId);
    }

    @Override
    public void pushUnreadCount(UUID userId, long unreadCount) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "UNREAD_COUNT_UPDATE");
        payload.put("unreadCount", unreadCount);

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                payload
        );
    }

    @Override
    public void pushNotificationRead(UUID userId, UUID notificationId, long unreadCount) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "NOTIFICATION_READ");
        payload.put("notificationId", notificationId);
        payload.put("unreadCount", unreadCount);

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                payload
        );
    }
}
