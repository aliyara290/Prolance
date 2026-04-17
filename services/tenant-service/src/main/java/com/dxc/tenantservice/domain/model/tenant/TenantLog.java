package com.dxc.tenantservice.domain.model.tenant;

import com.dxc.tenantservice.domain.model.enums.TenantLogAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantLog {
    private UUID id;
    private UUID tenantId;
    private UUID userId;
    private String entityType;
    private UUID entityId;
    private String IPAddress;
    private String userAgent;
    private TenantLogAction action;
    private LocalDateTime timestamp;
}
