package com.dxc.projectservice.application.dto.project.res;

import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;

import java.util.UUID;

public record ProjectsNamesResponse(
        UUID id,
        String name,
        String prefix,
        ProjectStatus status
) {
}
