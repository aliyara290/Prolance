package com.dxc.projectservice.application.port.in;

import com.dxc.projectservice.application.dto.resource.req.AddResourceRequest;
import com.dxc.projectservice.application.dto.resource.res.ResourceResponse;

import java.util.UUID;

public interface ResourceUseCase {
    ResourceResponse addResource(UUID projectId, AddResourceRequest request);
    void removeResource(UUID projectId, UUID resourceId);
}
