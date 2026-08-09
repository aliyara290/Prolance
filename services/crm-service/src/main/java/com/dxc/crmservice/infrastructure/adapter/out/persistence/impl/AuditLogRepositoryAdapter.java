package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.AuditLogRepository;
import com.dxc.crmservice.domain.model.entity.AuditLog;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.AuditLogEntity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.AuditLogRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepository {

    private final AuditLogRepositoryJpa jpa;

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogEntity entity = AuditLogEntity.builder()
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .message(auditLog.getMessage())
                .userId(auditLog.getUserId())
                .build();
        
        entity.setTenantId(auditLog.getTenantId());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        AuditLogEntity saved = jpa.save(entity);
        
        return AuditLog.builder()
                .id(saved.getId())
                .tenantId(saved.getTenantId())
                .userId(saved.getUserId())
                .action(saved.getAction())
                .entityType(saved.getEntityType())
                .entityId(saved.getEntityId())
                .message(saved.getMessage())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
