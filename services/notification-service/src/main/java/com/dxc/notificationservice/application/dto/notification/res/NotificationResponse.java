package com.dxc.notificationservice.application.dto.notification.res;

import com.dxc.notificationservice.domain.model.valueobject.NotificationCategory;
import com.dxc.notificationservice.domain.model.valueobject.NotificationPriority;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.domain.model.valueobject.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationPriority priority;
    private NotificationCategory category;
    private String icon;
    private String entityType;
    private UUID entityId;
    private String actionUrl;
    private ReadStatus readStatus;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
