package com.dxc.notificationservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.notificationservice.application.port.out.NotificationTemplateRepository;
import com.dxc.notificationservice.domain.model.entity.NotificationTemplate;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.jpa.NotificationTemplateRepositoryJpa;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.mapper.NotificationTemplatePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificationTemplateRepositoryAdapter implements NotificationTemplateRepository {

    private final NotificationTemplateRepositoryJpa jpaRepository;
    private final NotificationTemplatePersistenceMapper mapper;

    @Override
    public Optional<NotificationTemplate> findByType(NotificationType type) {
        return jpaRepository.findByType(type.name())
                .map(mapper::toDomain);
    }
}
