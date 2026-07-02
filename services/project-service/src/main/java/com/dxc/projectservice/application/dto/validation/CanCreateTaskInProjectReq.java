package com.dxc.projectservice.application.dto.validation;

import java.util.UUID;

public record CanCreateTaskInProjectReq(
        UUID userId
) {
}
