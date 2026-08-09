package com.dxc.attachmentservice.application.port.in;

import com.dxc.attachmentservice.application.dto.req.PresignedUploadRequest;
import com.dxc.attachmentservice.application.dto.res.AttachmentResponse;
import com.dxc.attachmentservice.application.dto.res.AttachmentSummaryResponse;
import com.dxc.attachmentservice.application.dto.res.PresignedUrlResponse;
import com.dxc.attachmentservice.domain.model.valueobject.EntityType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AttachmentUseCase {

    AttachmentResponse upload(MultipartFile file, EntityType entityType, UUID entityId);

    PresignedUrlResponse generatePresignedUploadUrl(PresignedUploadRequest request);

    AttachmentResponse getAttachment(UUID attachmentId);

    List<AttachmentSummaryResponse> getAttachmentsByEntity(EntityType entityType, UUID entityId);

    PresignedUrlResponse getDownloadUrl(UUID attachmentId);

    void deleteAttachment(UUID attachmentId);
}
