package com.dxc.taskservice.application.mapper;

import com.dxc.taskservice.application.dto.history.res.TaskStatusHistoryResponse;
import com.dxc.taskservice.domain.model.entity.TaskStatusHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskStatusHistoryApplicationMapper {

    public TaskStatusHistoryResponse toResponse(TaskStatusHistory history) {
        if (history == null) return null;

        return new TaskStatusHistoryResponse(
            history.getId(),
            history.getTaskId(),
            history.getOldStatus(),
            history.getNewStatus(),
            history.getChangedBy(),
            history.getChangedAt(),
            history.getComment()
        );
    }

    public List<TaskStatusHistoryResponse> toResponseList(List<TaskStatusHistory> histories) {
        if (histories == null) return null;
        return histories.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
