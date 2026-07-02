package com.dxc.projectservice.application.port.in;

import com.dxc.projectservice.application.dto.project.req.CreateProjectRequest;
import com.dxc.projectservice.application.dto.project.req.UpdateProjectRequest;
import com.dxc.projectservice.application.dto.project.res.ProjectResponse;
import com.dxc.projectservice.application.dto.project.res.ProjectsNamesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProjectUseCase {
    ProjectResponse createProject(CreateProjectRequest request);
    ProjectResponse getProjectById(UUID id);
    Page<ProjectResponse> getProjects(Pageable pageable);
    List<ProjectsNamesResponse> getProjectsNames(Pageable pageable);
    ProjectResponse updateProject(UUID id, UpdateProjectRequest request);
    void deleteProject(UUID id);
}
