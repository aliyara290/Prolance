package com.dxc.projectservice.application.dto.resource.req;

import com.dxc.projectservice.domain.model.valueobject.ResourceType;

public record AddResourceRequest(
    String name,
    String description,
    ResourceType type,
    String url
) {}
