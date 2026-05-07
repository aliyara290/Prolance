package com.dxc.projectservice.application.port.in;

import com.dxc.projectservice.domain.model.aggregate.Project;

public interface ProjectUseCase {
    Project save(Project project);
}
