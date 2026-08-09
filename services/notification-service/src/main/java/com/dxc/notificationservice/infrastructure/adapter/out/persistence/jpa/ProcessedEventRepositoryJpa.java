package com.dxc.notificationservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.notificationservice.infrastructure.adapter.out.persistence.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedEventRepositoryJpa extends JpaRepository<ProcessedEventEntity, UUID> {
}
