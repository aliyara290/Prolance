package com.dxc.notificationservice.application.service;

import com.dxc.notificationservice.application.mapper.NotificationApplicationMapper;
import com.dxc.notificationservice.application.port.out.EmailSender;
import com.dxc.notificationservice.application.port.out.NotificationPreferenceRepository;
import com.dxc.notificationservice.application.port.out.NotificationRepository;
import com.dxc.notificationservice.application.port.out.WebSocketNotifier;
import com.dxc.notificationservice.domain.model.aggregate.Notification;
import com.dxc.notificationservice.domain.model.entity.NotificationPreference;
import com.dxc.notificationservice.domain.model.entity.NotificationTemplate;
import com.dxc.notificationservice.domain.service.NotificationDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationDomainService domainService = new NotificationDomainService();
    private final NotificationApplicationMapper mapper;
    private final EmailSender emailSender;
    private final WebSocketNotifier webSocketNotifier;

    @Transactional
    public void deliver(
            NotificationTemplate template,
            UUID tenantId,
            UUID recipientId,
            UUID senderId,
            String entityType,
            UUID entityId,
            Map<String, String> variables,
            String recipientEmail,
            String recipientName
    ) {
        // 1. Check preferences
        NotificationPreference pref = preferenceRepository.getEffectivePreference(tenantId, recipientId, template.getType());
        
        Notification notification = null;

        // 2. In-App Delivery (always save if in-app enabled, or if it's a SYSTEM notification where pref can't be turned off)
        if (pref.isInAppEnabled() || template.getCategory() == com.dxc.notificationservice.domain.model.valueobject.NotificationCategory.SYSTEM) {
            notification = domainService.createNotificationFromTemplate(
                    template, tenantId, recipientId, senderId, entityType, entityId, variables
            );
            notification = notificationRepository.save(notification);
            
            // Push via WebSocket
            try {
                long unreadCount = notificationRepository.countUnread(tenantId, recipientId);
                webSocketNotifier.pushNewNotification(recipientId, mapper.toResponse(notification), unreadCount);
            } catch (Exception e) {
                log.warn("Failed to push notification to WebSocket for user {}", recipientId, e);
            }
        }

        // 3. Email Delivery (async)
        if (pref.isEmailEnabled() && recipientEmail != null && template.getEmailTemplateName() != null) {
            sendEmailAsync(template, notification, variables, recipientEmail, recipientName);
        }
        
    }

    @Async
    protected void sendEmailAsync(
            NotificationTemplate template,
            Notification notification,
            Map<String, String> variables,
            String recipientEmail,
            String recipientName
    ) {
        try {
            Map<String, Object> model = new HashMap<>(variables);
            model.put("recipientName", recipientName);
            if (notification != null) {
                model.put("title", notification.getTitle());
                model.put("message", notification.getMessage());
                model.put("actionUrl", notification.getActionUrl());
            }

            String subject = template.getEmailSubjectTemplate();
            if (subject != null) {
                for (Map.Entry<String, String> entry : variables.entrySet()) {
                    if (entry.getValue() != null) {
                        subject = subject.replaceAll("\\$?\\{" + entry.getKey() + "\\}", java.util.regex.Matcher.quoteReplacement(entry.getValue()));
                    }
                }
                subject = subject.replaceAll("\\$?\\{.*?\\}", "");
            }

            emailSender.sendHtmlEmail(recipientEmail, subject, template.getEmailTemplateName(), model);
        } catch (Exception e) {
            log.error("Failed to send email notification to {}", recipientEmail, e);
        }
    }
}
