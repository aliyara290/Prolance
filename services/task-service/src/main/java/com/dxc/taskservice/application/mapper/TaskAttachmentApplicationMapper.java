package com.dxc.taskservice.application.mapper;

import com.dxc.taskservice.application.dto.attachment.res.TaskAttachmentResponse;
import com.dxc.taskservice.domain.model.entity.TaskAttachment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskAttachmentApplicationMapper {

    public TaskAttachmentResponse toResponse(TaskAttachment attachment) {
        if (attachment == null) return null;

        return new TaskAttachmentResponse(
            attachment.getId(),
            attachment.getTaskId(),
            attachment.getFileUrl(),
            attachment.getFileName(),
            attachment.getFileType(),
            attachment.getUploadedBy(),
            attachment.getCreatedAt()
        );
    }

    public List<TaskAttachmentResponse> toResponseList(List<TaskAttachment> attachments) {
        if (attachments == null) return null;
        return attachments.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
