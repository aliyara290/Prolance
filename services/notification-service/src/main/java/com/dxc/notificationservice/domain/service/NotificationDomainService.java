package com.dxc.notificationservice.domain.service;

import com.dxc.notificationservice.domain.model.aggregate.Notification;
import com.dxc.notificationservice.domain.model.entity.NotificationTemplate;
import com.dxc.notificationservice.domain.model.valueobject.ReadStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationDomainService {

    public Notification createNotificationFromTemplate(
            NotificationTemplate template,
            UUID tenantId,
            UUID recipientId,
            UUID senderId,
            String entityType,
            UUID entityId,
            Map<String, String> variables
    ) {
        String title = resolveTemplate(template.getTitleTemplate(), variables);
        String message = resolveTemplate(template.getMessageTemplate(), variables);
        String actionUrl = template.getDefaultActionUrlPattern() != null ? 
                resolveTemplate(template.getDefaultActionUrlPattern(), variables) : null;

        return Notification.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .recipientId(recipientId)
                .senderId(senderId)
                .title(title)
                .message(message)
                .type(template.getType())
                .priority(template.getDefaultPriority())
                .category(template.getCategory())
                .icon(template.getIcon())
                .entityType(entityType)
                .entityId(entityId)
                .actionUrl(actionUrl)
                .readStatus(ReadStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String resolveTemplate(String template, Map<String, String> variables) {
        if (template == null || variables == null || variables.isEmpty()) {
            return template;
        }

        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "\\$?\\{" + entry.getKey() + "\\}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            result = result.replaceAll(placeholder, Matcher.quoteReplacement(value));
        }

        // Remove any remaining unresolved placeholders
        result = result.replaceAll("\\$?\\{.*?\\}", "");
        
        return result.trim();
    }
}
