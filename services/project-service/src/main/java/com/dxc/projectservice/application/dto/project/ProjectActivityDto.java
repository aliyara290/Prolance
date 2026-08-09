package com.dxc.projectservice.application.dto.project;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectActivityDto(
        UUID id,
        String type,
        String payload,
        LocalDateTime occurredOn
) {
}
