package com.dxc.notificationservice.application.service;

import com.dxc.notificationservice.application.dto.notification.res.NotificationResponse;
import com.dxc.notificationservice.application.mapper.NotificationApplicationMapper;
import com.dxc.notificationservice.application.port.in.NotificationUseCase;
import com.dxc.notificationservice.application.port.out.NotificationRepository;
import com.dxc.notificationservice.application.port.out.WebSocketNotifier;
import com.dxc.notificationservice.domain.exception.RecordNotFoundException;
import com.dxc.notificationservice.domain.model.aggregate.Notification;
import com.dxc.notificationservice.domain.model.valueobject.NotificationCategory;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.domain.model.valueobject.ReadStatus;
import com.dxc.notificationservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService implements NotificationUseCase {

    private final NotificationRepository notificationRepository;
    private final NotificationApplicationMapper mapper;
    private final WebSocketNotifier webSocketNotifier;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(
            UUID recipientId,
            NotificationType type,
            ReadStatus readStatus,
            NotificationCategory category,
            Pageable pageable) {
        UUID tenantId = UUID.fromString(TenantContextHolder.getTenantId());
        
        return notificationRepository.findByFilters(tenantId, recipientId, type, readStatus, category, pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID recipientId) {
        UUID tenantId = UUID.fromString(TenantContextHolder.getTenantId());
        return notificationRepository.countUnread(tenantId, recipientId);
    }

    @Override
    @Transactional
    public void markAsRead(UUID id, UUID recipientId) {
        UUID tenantId = UUID.fromString(TenantContextHolder.getTenantId());
        Notification notification = notificationRepository.findById(id, tenantId)
                .orElseThrow(() -> new RecordNotFoundException("Notification not found with ID: " + id));
        
        notification.markAsRead();
        notificationRepository.save(notification);
        
        long count = notificationRepository.countUnread(tenantId, recipientId);
        webSocketNotifier.pushNotificationRead(recipientId, id, count);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID recipientId) {
        UUID tenantId = UUID.fromString(TenantContextHolder.getTenantId());
        notificationRepository.markAllAsRead(tenantId, recipientId);
        
        webSocketNotifier.pushUnreadCount(recipientId, 0);
    }

    @Override
    @Transactional
    public void archive(UUID id, UUID recipientId) {
        UUID tenantId = UUID.fromString(TenantContextHolder.getTenantId());
        Notification notification = notificationRepository.findById(id, tenantId)
                .orElseThrow(() -> new RecordNotFoundException("Notification not found with ID: " + id));
        
        notification.archive();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID recipientId) {
        UUID tenantId = UUID.fromString(TenantContextHolder.getTenantId());
        notificationRepository.delete(id, tenantId);
    }
}
