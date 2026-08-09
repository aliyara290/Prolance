package com.dxc.notificationservice.application.port.in;

import com.dxc.notificationservice.application.dto.notification.req.UpdatePreferenceRequest;
import com.dxc.notificationservice.application.dto.preference.res.NotificationPreferenceResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationPreferenceUseCase {
    List<NotificationPreferenceResponse> getPreferences(UUID userId);
    List<NotificationPreferenceResponse> updatePreferences(UUID userId, List<UpdatePreferenceRequest> requests);
}
