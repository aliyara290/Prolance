package com.dxc.projectservice.application.dto.project.req;

import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;

public record ChangeProjectStatusRequest(
    ProjectStatus newStatus,
    String comment
) {}
