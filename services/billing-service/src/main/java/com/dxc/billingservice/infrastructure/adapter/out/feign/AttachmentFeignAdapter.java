package com.dxc.billingservice.infrastructure.adapter.out.feign;

import com.dxc.billingservice.application.port.out.feign.AttachmentFeignPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttachmentFeignAdapter implements AttachmentFeignPort {

    private static final Logger log = LoggerFactory.getLogger(AttachmentFeignAdapter.class);

    private final AttachmentFeignClient attachmentFeignClient;

    @Override
    public AttachmentResponseDTO uploadFile(byte[] fileBytes, String fileName, String contentType, String entityType, UUID entityId) {
        log.info("Uploading file {} to attachment-service for entity: {}/{}", fileName, entityType, entityId);
        ByteArrayMultipartFile multipartFile = new ByteArrayMultipartFile(
                "file",
                fileName,
                contentType,
                fileBytes
        );
        return attachmentFeignClient.uploadFile(multipartFile, fileName, contentType, entityType, entityId).getData();
    }

    @Override
    public String getDownloadUrl(UUID attachmentId) {
        return attachmentFeignClient.getDownloadUrl(attachmentId);
    }
}
