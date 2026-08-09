package com.dxc.notificationservice.application.service;

import com.dxc.notificationservice.application.dto.notification.req.UpdatePreferenceRequest;
import com.dxc.notificationservice.application.dto.preference.res.NotificationPreferenceResponse;
import com.dxc.notificationservice.application.mapper.NotificationPreferenceApplicationMapper;
import com.dxc.notificationservice.application.port.in.NotificationPreferenceUseCase;
import com.dxc.notificationservice.application.port.out.NotificationPreferenceRepository;
import com.dxc.notificationservice.domain.model.entity.NotificationPreference;
import com.dxc.notificationservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPreferenceService implements NotificationPreferenceUseCase {

    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationPreferenceApplicationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationPreferenceResponse> getPreferences(UUID userId) {
        UUID tenantId = UUID.fromString(TenantContextHolder.getTenantId());
        return preferenceRepository.findByUserId(tenantId, userId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<NotificationPreferenceResponse> updatePreferences(UUID userId, List<UpdatePreferenceRequest> requests) {
        UUID tenantId = UUID.fromString(TenantContextHolder.getTenantId());
        
        Map<com.dxc.notificationservice.domain.model.valueobject.NotificationType, NotificationPreference> existingPreferences = 
                preferenceRepository.findByUserId(tenantId, userId).stream()
                        .collect(Collectors.toMap(NotificationPreference::getNotificationType, Function.identity()));
        
        List<NotificationPreference> toSave = new ArrayList<>();
        
        for (UpdatePreferenceRequest req : requests) {
            NotificationPreference pref = existingPreferences.get(req.getNotificationType());
            
            if (pref == null) {
                pref = NotificationPreference.builder()
                        .id(UUID.randomUUID())
                        .tenantId(tenantId)
                        .userId(userId)
                        .notificationType(req.getNotificationType())
                        .inAppEnabled(req.isInAppEnabled())
                        .emailEnabled(req.isEmailEnabled())
                        .pushEnabled(req.isPushEnabled())
                        .smsEnabled(req.isSmsEnabled())
                        .build();
            } else {
                pref.updatePreferences(
                        req.isInAppEnabled(),
                        req.isEmailEnabled(),
                        req.isPushEnabled(),
                        req.isSmsEnabled()
                );
            }
            toSave.add(pref);
        }
        
        return preferenceRepository.saveAll(toSave).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
