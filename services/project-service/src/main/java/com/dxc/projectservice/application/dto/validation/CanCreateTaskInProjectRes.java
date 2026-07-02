package com.dxc.projectservice.application.dto.validation;

public record CanCreateTaskInProjectRes(
        boolean allowed,
        String message
) {
}
