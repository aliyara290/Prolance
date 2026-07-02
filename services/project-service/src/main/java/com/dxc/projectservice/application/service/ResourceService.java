package com.dxc.projectservice.application.service;

import com.dxc.projectservice.application.dto.resource.req.AddResourceRequest;
import com.dxc.projectservice.application.dto.resource.res.ResourceResponse;
import com.dxc.projectservice.application.mapper.ProjectResourceApplicationMapper;
import com.dxc.projectservice.application.port.in.ResourceUseCase;
import com.dxc.projectservice.application.port.out.DomainEventPublisher;
import com.dxc.projectservice.application.port.out.ProjectRepository;
import com.dxc.projectservice.application.security.TenantGuard;
import com.dxc.projectservice.domain.exception.RecordNotFoundException;
import com.dxc.projectservice.domain.model.aggregate.Project;
import com.dxc.projectservice.domain.model.entity.ProjectResource;
import com.dxc.projectservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceService implements ResourceUseCase {

    private final ProjectRepository projectRepository;
    private final DomainEventPublisher eventPublisher;
    private final ProjectResourceApplicationMapper resourceMapper;
    private final TenantGuard tenantGuard;

    @Override
    @Transactional
    public ResourceResponse addResource(UUID projectId, AddResourceRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();
        
        Project project = getProject(projectId, tenantId);
        project.addResource(request.name(), request.description(), request.type(), request.url(), actionBy);
        
        projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();
        
        ProjectResource addedResource = project.getResources().stream()
                .filter(r -> r.getName().equals(request.name()))
                .max(Comparator.comparing(ProjectResource::getCreatedAt))
                .orElseThrow();
                
        return resourceMapper.toResponse(addedResource);
    }

    @Override
    @Transactional
    public void removeResource(UUID projectId, UUID resourceId) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();
        
        Project project = getProject(projectId, tenantId);
        project.removeResource(resourceId, actionBy);
        
        projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();
    }

    private Project getProject(UUID id, UUID tenantId) {
        return projectRepository.findById(id, tenantId)
                .orElseThrow(() -> new RecordNotFoundException("Project not found with id " + id));
    }

    private UUID getTenantIdAndVerify() {
        String tenantIdStr = TenantContextHolder.getTenantId();
        if (tenantIdStr == null) {
            throw new IllegalArgumentException("Tenant context is missing");
        }
        UUID tenantId = UUID.fromString(tenantIdStr);
        tenantGuard.ensureTenantIsActive(tenantId);
        return tenantId;
    }
}
