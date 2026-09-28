package com.dxc.billingservice.application.port.out.feign;

import java.util.UUID;

public interface AttachmentFeignPort {
    AttachmentResponseDTO uploadFile(byte[] fileBytes, String fileName, String contentType, String entityType, UUID entityId);
    String getDownloadUrl(UUID attachmentId);

    record AttachmentResponseDTO(
            UUID id,
            String originalFileName,
            String objectKey,
            String bucketName,
            long size
    ) {}
}