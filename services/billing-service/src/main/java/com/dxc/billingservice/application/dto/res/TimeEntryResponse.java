package com.dxc.billingservice.application.dto.res;

import java.time.LocalDateTime;
import java.util.UUID;

public record TimeEntryResponse(
        UUID id,
        UUID projectId,
        UUID taskId,
        UUID userId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long durationMinutes,
        String description,
        boolean billable
) {}
