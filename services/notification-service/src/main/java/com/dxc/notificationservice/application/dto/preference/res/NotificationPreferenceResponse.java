package com.dxc.notificationservice.application.dto.preference.res;

import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceResponse {
    private UUID id;
    private NotificationType notificationType;
    private boolean inAppEnabled;
    private boolean emailEnabled;
    private boolean pushEnabled;
    private boolean smsEnabled;
}
