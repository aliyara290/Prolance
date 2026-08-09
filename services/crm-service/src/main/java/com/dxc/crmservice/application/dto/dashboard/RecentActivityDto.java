package com.dxc.crmservice.application.dto.dashboard;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecentActivityDto(
        UUID id,
        String action,
        String message,
        String entityType,
        LocalDateTime createdAt
) {}
