package com.dxc.projectservice.application.port.in.validation;

import com.dxc.projectservice.application.dto.validation.CanCreateTaskInProjectReq;
import com.dxc.projectservice.application.dto.validation.CanCreateTaskInProjectRes;

import java.util.UUID;

public interface ValidationUseCase {
    CanCreateTaskInProjectRes canCreateTaskInProject(UUID projectId, CanCreateTaskInProjectReq req);
}
