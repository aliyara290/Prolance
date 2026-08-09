package com.dxc.notificationservice.domain.model.entity;

import com.dxc.notificationservice.domain.model.valueobject.NotificationCategory;
import com.dxc.notificationservice.domain.model.valueobject.NotificationPriority;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationTemplate {
    private final UUID id;
    private final NotificationType type;
    private final String titleTemplate;
    private final String messageTemplate;
    private final String emailSubjectTemplate;
    private final String emailTemplateName;
    private final String icon;
    private final String defaultActionUrlPattern;
    private final NotificationCategory category;
    private final NotificationPriority defaultPriority;
}
