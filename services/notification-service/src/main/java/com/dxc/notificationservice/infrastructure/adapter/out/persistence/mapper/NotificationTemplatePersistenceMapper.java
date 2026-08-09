package com.dxc.notificationservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.notificationservice.domain.model.entity.NotificationTemplate;
import com.dxc.notificationservice.domain.model.valueobject.NotificationCategory;
import com.dxc.notificationservice.domain.model.valueobject.NotificationPriority;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.entity.NotificationTemplateEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationTemplatePersistenceMapper {

    public NotificationTemplate toDomain(NotificationTemplateEntity entity) {
        if (entity == null) return null;

        return NotificationTemplate.builder()
                .id(entity.getId())
                .type(NotificationType.valueOf(entity.getType()))
                .titleTemplate(entity.getTitleTemplate())
                .messageTemplate(entity.getMessageTemplate())
                .emailSubjectTemplate(entity.getEmailSubjectTemplate())
                .emailTemplateName(entity.getEmailTemplateName())
                .icon(entity.getIcon())
                .defaultActionUrlPattern(entity.getDefaultActionUrlPattern())
                .category(NotificationCategory.valueOf(entity.getCategory()))
                .defaultPriority(NotificationPriority.valueOf(entity.getDefaultPriority()))
                .build();
    }
}
