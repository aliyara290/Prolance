package com.dxc.notificationservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.notificationservice.application.port.out.ProcessedEventRepository;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.entity.ProcessedEventEntity;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.jpa.ProcessedEventRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProcessedEventRepositoryAdapter implements ProcessedEventRepository {

    private final ProcessedEventRepositoryJpa jpaRepository;

    @Override
    public boolean existsById(UUID eventId) {
        return jpaRepository.existsById(eventId);
    }

    @Override
    public void markAsProcessed(UUID eventId, String eventType) {
        ProcessedEventEntity entity = ProcessedEventEntity.builder()
                .eventId(eventId)
                .eventType(eventType)
                .processedAt(LocalDateTime.now())
                .build();
        jpaRepository.save(entity);
    }
}
