package com.dxc.notificationservice.domain.model.aggregate;

import com.dxc.notificationservice.domain.exception.BusinessRuleException;
import com.dxc.notificationservice.domain.model.valueobject.NotificationCategory;
import com.dxc.notificationservice.domain.model.valueobject.NotificationPriority;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.domain.model.valueobject.ReadStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification {
    private final UUID id;
    private final UUID tenantId;
    private final UUID recipientId;
    private final UUID senderId;
    private final String title;
    private final String message;
    private final NotificationType type;
    private final NotificationPriority priority;
    private final NotificationCategory category;
    private final String icon;
    private final String entityType;
    private final UUID entityId;
    private final String actionUrl;
    private ReadStatus readStatus;
    private LocalDateTime readAt;
    private LocalDateTime archivedAt;
    private final LocalDateTime createdAt;

    public void markAsRead() {
        if (this.readStatus == ReadStatus.READ) {
            return;
        }
        this.readStatus = ReadStatus.READ;
        this.readAt = LocalDateTime.now();
    }

    public void archive() {
        if (this.archivedAt != null) {
            throw new BusinessRuleException("Notification is already archived");
        }
        this.archivedAt = LocalDateTime.now();
    }
}
