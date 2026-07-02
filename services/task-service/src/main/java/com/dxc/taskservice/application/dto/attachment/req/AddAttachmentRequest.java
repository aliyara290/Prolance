package com.dxc.taskservice.application.dto.attachment.req;

import jakarta.validation.constraints.NotBlank;

public record AddAttachmentRequest(
    @NotBlank(message = "File URL is required")
    String fileUrl,

    @NotBlank(message = "File name is required")
    String fileName,

    String fileType
) {}
