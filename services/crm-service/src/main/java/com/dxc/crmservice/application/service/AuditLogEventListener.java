package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.port.out.AuditLogRepository;
import com.dxc.crmservice.domain.event.AuditLogEvent;
import com.dxc.crmservice.domain.model.entity.AuditLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogEventListener {

    private final AuditLogRepository auditLogRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAuditLogEvent(AuditLogEvent event) {
        log.info("Recording audit log: {} {} ({})", event.action(), event.entityType(), event.entityId());
        AuditLog auditLog = AuditLog.builder()
                .tenantId(event.tenantId())
                .userId(event.userId())
                .action(event.action())
                .entityType(event.entityType())
                .entityId(event.entityId())
                .message(event.message())
                .build();

        auditLogRepository.save(auditLog);
    }
}
