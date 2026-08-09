package com.dxc.notificationservice.application.dto.notification.req;

import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreferenceRequest {
    @NotNull(message = "Notification type is required")
    private NotificationType notificationType;
    
    private boolean inAppEnabled;
    private boolean emailEnabled;
    private boolean pushEnabled;
    private boolean smsEnabled;
}
