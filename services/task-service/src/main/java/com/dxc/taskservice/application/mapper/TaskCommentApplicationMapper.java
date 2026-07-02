package com.dxc.taskservice.application.mapper;

import com.dxc.taskservice.application.dto.comment.res.TaskCommentResponse;
import com.dxc.taskservice.domain.model.entity.TaskComment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskCommentApplicationMapper {

    public TaskCommentResponse toResponse(TaskComment comment) {
        if (comment == null) return null;

        return new TaskCommentResponse(
            comment.getId(),
            comment.getTaskId(),
            comment.getUserId(),
            comment.getContent(),
            comment.getCreatedAt(),
            comment.getUpdatedAt()
        );
    }

    public List<TaskCommentResponse> toResponseList(List<TaskComment> comments) {
        if (comments == null) return null;
        return comments.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
