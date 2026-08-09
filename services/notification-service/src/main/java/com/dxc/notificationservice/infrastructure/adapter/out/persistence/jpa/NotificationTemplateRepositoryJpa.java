package com.dxc.notificationservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.notificationservice.infrastructure.adapter.out.persistence.entity.NotificationTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationTemplateRepositoryJpa extends JpaRepository<NotificationTemplateEntity, UUID> {
    Optional<NotificationTemplateEntity> findByType(String type);
}
