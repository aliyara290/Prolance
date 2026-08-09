package com.dxc.notificationservice.domain.model.entity;

import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationPreference {
    private final UUID id;
    private final UUID tenantId;
    private final UUID userId;
    private final NotificationType notificationType;
    private boolean inAppEnabled;
    private boolean emailEnabled;
    private boolean pushEnabled;
    private boolean smsEnabled;

    public void updatePreferences(boolean inApp, boolean email, boolean push, boolean sms) {
        this.inAppEnabled = inApp;
        this.emailEnabled = email;
        this.pushEnabled = push;
        this.smsEnabled = sms;
    }
}
