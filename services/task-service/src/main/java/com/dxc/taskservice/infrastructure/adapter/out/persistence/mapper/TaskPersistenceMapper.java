package com.dxc.taskservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.taskservice.domain.model.aggregate.Task;
import com.dxc.taskservice.domain.model.valueobject.TaskTimeline;
import com.dxc.taskservice.infrastructure.adapter.out.persistence.entity.TaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class TaskPersistenceMapper {

    private final TaskAssignmentPersistenceMapper assignmentMapper;
    private final TaskCommentPersistenceMapper commentMapper;
    private final TaskAttachmentPersistenceMapper attachmentMapper;
    private final TaskStatusHistoryPersistenceMapper statusHistoryMapper;
    private final TaskDependencyPersistenceMapper dependencyMapper;

    public TaskEntity toEntity(Task domain) {
        if (domain == null) return null;

        TaskEntity entity = new TaskEntity();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setProjectId(domain.getProjectId());
        entity.setMilestoneId(domain.getMilestoneId());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setType(domain.getType());
        entity.setPriority(domain.getPriority());
        entity.setStatus(domain.getStatus());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setReporterId(domain.getReporterId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getTimeline() != null) {
            entity.setStartDate(domain.getTimeline().startDate());
            entity.setDueDate(domain.getTimeline().dueDate());
            entity.setCompletedAt(domain.getTimeline().completedAt());
        }

        entity.setAssignments(assignmentMapper.toEntityList(domain.getAssignments()));
        entity.setComments(commentMapper.toEntityList(domain.getComments()));
        entity.setAttachments(attachmentMapper.toEntityList(domain.getAttachments()));
        entity.setStatusHistory(statusHistoryMapper.toEntityList(domain.getStatusHistory()));
        entity.setDependencies(dependencyMapper.toEntityList(domain.getDependencies()));

        // Set parent references for bidirectional JPA
        if (entity.getAssignments() != null) entity.getAssignments().forEach(a -> a.setTaskId(entity.getId()));
        if (entity.getComments() != null) entity.getComments().forEach(c -> c.setTaskId(entity.getId()));
        if (entity.getAttachments() != null) entity.getAttachments().forEach(a -> a.setTaskId(entity.getId()));
        if (entity.getStatusHistory() != null) entity.getStatusHistory().forEach(h -> h.setTaskId(entity.getId()));
        if (entity.getDependencies() != null) entity.getDependencies().forEach(d -> d.setTaskId(entity.getId()));

        return entity;
    }

    public Task toDomain(TaskEntity entity) {
        if (entity == null) return null;

        TaskTimeline timeline = null;
        if (entity.getStartDate() != null) {
            timeline = new TaskTimeline(
                    entity.getStartDate(),
                    entity.getDueDate(),
                    entity.getCompletedAt()
            );
        }

        return Task.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .projectId(entity.getProjectId())
                .milestoneId(entity.getMilestoneId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .type(entity.getType())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .timeline(timeline)
                .createdBy(entity.getCreatedBy())
                .reporterId(entity.getReporterId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .assignments(
                        entity.getAssignments() != null ?
                                assignmentMapper.toDomainList(entity.getAssignments()) : new ArrayList<>()
                )
                .comments(
                        entity.getComments() != null ?
                                commentMapper.toDomainList(entity.getComments()) : new ArrayList<>()
                )
                .attachments(
                        entity.getAttachments() != null ?
                                attachmentMapper.toDomainList(entity.getAttachments()) : new ArrayList<>()
                )
                .statusHistory(
                        entity.getStatusHistory() != null ?
                                statusHistoryMapper.toDomainList(entity.getStatusHistory()) : new ArrayList<>()
                )
                .dependencies(
                        entity.getDependencies() != null ?
                                dependencyMapper.toDomainList(entity.getDependencies()) : new ArrayList<>()
                )
                .build();
    }
}
