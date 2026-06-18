package com.dxc.projectservice.application.mapper;

import com.dxc.projectservice.application.dto.resource.res.ResourceResponse;
import com.dxc.projectservice.domain.model.entity.ProjectResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectResourceApplicationMapper {

    public ResourceResponse toResponse(ProjectResource resource) {
        if (resource == null) return null;
        
        return new ResourceResponse(
            resource.getId(),
            resource.getProjectId(),
            resource.getName(),
            resource.getDescription(),
            resource.getType(),
            resource.getUrl(),
            resource.getCreatedAt()
        );
    }
    
    public List<ResourceResponse> toResponseList(List<ProjectResource> resources) {
        if (resources == null) return null;
        return resources.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
