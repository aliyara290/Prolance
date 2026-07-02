package com.dxc.taskservice.infrastructure.adapter.out.feign.dto;

import java.util.UUID;

public record CanCreateTaskRequest(
        UUID userId
) {
}
