package com.dxc.taskservice.application.mapper;

import com.dxc.taskservice.application.dto.task.res.TaskResponse;
import com.dxc.taskservice.domain.model.aggregate.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskApplicationMapper {

    private final TaskAssignmentApplicationMapper assignmentMapper;
    private final TaskCommentApplicationMapper commentMapper;
    private final TaskAttachmentApplicationMapper attachmentMapper;
    private final TaskDependencyApplicationMapper dependencyMapper;
    private final TaskStatusHistoryApplicationMapper statusHistoryMapper;

    public TaskResponse toResponse(Task task) {
        if (task == null) return null;

        return new TaskResponse(
            task.getId(),
            task.getProjectId(),
            task.getMilestoneId(),
            task.getTitle(),
            task.getDescription(),
            task.getType(),
            task.getPriority(),
            task.getStatus(),
            task.getTimeline() != null ? task.getTimeline().startDate() : null,
            task.getTimeline() != null ? task.getTimeline().dueDate() : null,
            task.getTimeline() != null ? task.getTimeline().completedAt() : null,
            task.getCreatedBy(),
            task.getReporterId(),
            task.getCreatedAt(),
            task.getUpdatedAt(),
            assignmentMapper.toResponseList(task.getAssignments()),
            commentMapper.toResponseList(task.getComments()),
            attachmentMapper.toResponseList(task.getAttachments()),
            dependencyMapper.toResponseList(task.getDependencies()),
            statusHistoryMapper.toResponseList(task.getStatusHistory())
        );
    }

    public List<TaskResponse> toResponseList(List<Task> tasks) {
        if (tasks == null) return null;
        return tasks.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
