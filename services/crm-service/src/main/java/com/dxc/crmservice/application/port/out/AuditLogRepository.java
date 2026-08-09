package com.dxc.crmservice.application.port.out;

import com.dxc.crmservice.domain.model.entity.AuditLog;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
}
