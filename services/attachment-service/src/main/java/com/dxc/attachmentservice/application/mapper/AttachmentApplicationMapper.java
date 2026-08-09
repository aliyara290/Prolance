package com.dxc.attachmentservice.application.mapper;

import com.dxc.attachmentservice.application.dto.res.AttachmentResponse;
import com.dxc.attachmentservice.application.dto.res.AttachmentSummaryResponse;
import com.dxc.attachmentservice.domain.model.entity.Attachment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AttachmentApplicationMapper {

    public AttachmentResponse toResponse(Attachment attachment) {
        if (attachment == null) return null;

        return new AttachmentResponse(
                attachment.getId(),
                attachment.getEntityType(),
                attachment.getEntityId(),
                attachment.getOriginalFileName(),
                attachment.getStoredFileName(),
                attachment.getObjectKey(),
                attachment.getBucketName(),
                attachment.getContentType(),
                attachment.getSize(),
                attachment.getUploadedBy(),
                attachment.getUploadedAt()
        );
    }

    public AttachmentSummaryResponse toSummaryResponse(Attachment attachment) {
        if (attachment == null) return null;

        return new AttachmentSummaryResponse(
                attachment.getId(),
                attachment.getEntityType(),
                attachment.getEntityId(),
                attachment.getOriginalFileName(),
                attachment.getContentType(),
                attachment.getSize(),
                attachment.getUploadedBy(),
                attachment.getUploadedAt()
        );
    }

    public List<AttachmentSummaryResponse> toSummaryResponseList(List<Attachment> attachments) {
        if (attachments == null) return List.of();
        return attachments.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }
}
