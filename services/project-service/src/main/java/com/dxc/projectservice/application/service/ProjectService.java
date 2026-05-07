package com.dxc.projectservice.application.service;

import com.dxc.projectservice.application.port.in.ProjectUseCase;
import com.dxc.projectservice.application.port.out.DomainEventPublisher;
import com.dxc.projectservice.application.port.out.ProjectRepository;
import com.dxc.projectservice.domain.model.aggregate.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectUseCase {

    private final ProjectRepository projectRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public Project save(Project project) {
        Project savedProject = projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();
        return savedProject;
    }
}
