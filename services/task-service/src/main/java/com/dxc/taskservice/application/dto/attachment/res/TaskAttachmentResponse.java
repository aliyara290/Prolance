package com.dxc.taskservice.application.dto.attachment.res;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskAttachmentResponse(
    UUID id,
    UUID taskId,
    String fileUrl,
    String fileName,
    String fileType,
    UUID uploadedBy,
    LocalDateTime createdAt
) {}
