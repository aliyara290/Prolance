package com.dxc.notificationservice.application.port.out;

import com.dxc.notificationservice.domain.model.entity.NotificationTemplate;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;

import java.util.Optional;

public interface NotificationTemplateRepository {
    Optional<NotificationTemplate> findByType(NotificationType type);
}
