package com.dxc.attachmentservice.application.service;

import com.dxc.attachmentservice.application.dto.req.PresignedUploadRequest;
import com.dxc.attachmentservice.application.dto.res.AttachmentResponse;
import com.dxc.attachmentservice.application.dto.res.AttachmentSummaryResponse;
import com.dxc.attachmentservice.application.dto.res.PresignedUrlResponse;
import com.dxc.attachmentservice.application.mapper.AttachmentApplicationMapper;
import com.dxc.attachmentservice.application.port.in.AttachmentUseCase;
import com.dxc.attachmentservice.application.port.out.AttachmentRepository;
import com.dxc.attachmentservice.application.port.out.StoragePort;
import com.dxc.attachmentservice.domain.exception.AttachmentNotFoundException;
import com.dxc.attachmentservice.domain.model.entity.Attachment;
import com.dxc.attachmentservice.domain.model.valueobject.EntityType;
import com.dxc.attachmentservice.infrastructure.config.AwsS3Properties;
import com.dxc.attachmentservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class AttachmentService implements AttachmentUseCase {

    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofMinutes(15);

    private final AttachmentRepository attachmentRepository;
    private final StoragePort storagePort;
    private final FileValidator fileValidator;
    private final AttachmentApplicationMapper mapper;
    private final AwsS3Properties s3Properties;

    @Override
    public AttachmentResponse upload(MultipartFile file, EntityType entityType, UUID entityId) {
        UUID tenantId = getTenantId();
        UUID userId = TenantContextHolder.getUserId();

        log.info("Upload started: entityType={}, entityId={}, fileName={}, size={}, user={}",
                entityType, entityId, file.getOriginalFilename(), file.getSize(), userId);

        fileValidator.validate(file);

        String storedFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String objectKey = buildObjectKey(entityType, entityId, storedFileName);

        try {
            storagePort.upload(objectKey, file.getInputStream(), file.getSize(), file.getContentType());
        } catch (IOException e) {
            log.error("Failed to read file input stream: {}", file.getOriginalFilename(), e);
            throw new com.dxc.attachmentservice.domain.exception.StorageException(
                    "Failed to read uploaded file", e);
        }

        Attachment attachment = Attachment.create(
                tenantId,
                entityType,
                entityId,
                file.getOriginalFilename(),
                storedFileName,
                objectKey,
                s3Properties.getBucketName(),
                file.getContentType(),
                file.getSize(),
                userId
        );

        Attachment saved = attachmentRepository.save(attachment);

        log.info("Upload completed: attachmentId={}, objectKey={}", saved.getId(), objectKey);
        return mapper.toResponse(saved);
    }

    @Override
    public PresignedUrlResponse generatePresignedUploadUrl(PresignedUploadRequest request) {
        UUID tenantId = getTenantId();
        log.info("Generating presigned upload URL: entityType={}, entityId={}, fileName={}",
                request.entityType(), request.entityId(), request.fileName());

        fileValidator.validateContentType(request.contentType());

        String storedFileName = UUID.randomUUID() + "-" + request.fileName();
        String objectKey = buildObjectKey(request.entityType(), request.entityId(), storedFileName);

        String url = storagePort.generatePresignedUploadUrl(objectKey, request.contentType(), PRESIGNED_URL_EXPIRATION);

        return new PresignedUrlResponse(url, objectKey, Instant.now().plus(PRESIGNED_URL_EXPIRATION));
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentResponse getAttachment(UUID attachmentId) {
        UUID tenantId = getTenantId();
        Attachment attachment = loadAttachment(attachmentId, tenantId);
        return mapper.toResponse(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentSummaryResponse> getAttachmentsByEntity(EntityType entityType, UUID entityId) {
        UUID tenantId = getTenantId();
        List<Attachment> attachments = attachmentRepository.findByEntityTypeAndEntityId(entityType, entityId, tenantId);
        return mapper.toSummaryResponseList(attachments);
    }

    @Override
    @Transactional(readOnly = true)
    public PresignedUrlResponse getDownloadUrl(UUID attachmentId) {
        UUID tenantId = getTenantId();
        Attachment attachment = loadAttachment(attachmentId, tenantId);

        log.info("Generating presigned download URL: attachmentId={}, objectKey={}", attachmentId, attachment.getObjectKey());

        String url = storagePort.generatePresignedDownloadUrl(attachment.getObjectKey(), PRESIGNED_URL_EXPIRATION);

        return new PresignedUrlResponse(url, attachment.getObjectKey(), Instant.now().plus(PRESIGNED_URL_EXPIRATION));
    }

    @Override
    public void deleteAttachment(UUID attachmentId) {
        UUID tenantId = getTenantId();
        Attachment attachment = loadAttachment(attachmentId, tenantId);

        log.info("Deleting attachment: attachmentId={}, objectKey={}", attachmentId, attachment.getObjectKey());

        storagePort.delete(attachment.getObjectKey());
        attachmentRepository.delete(attachmentId, tenantId);

        log.info("Attachment deleted successfully: attachmentId={}", attachmentId);
    }

    private Attachment loadAttachment(UUID attachmentId, UUID tenantId) {
        return attachmentRepository.findById(attachmentId, tenantId)
                .orElseThrow(() -> new AttachmentNotFoundException(
                        "Attachment not found with id: " + attachmentId));
    }

    private String buildObjectKey(EntityType entityType, UUID entityId, String storedFileName) {
        String prefix = entityType.name().toLowerCase();
        return prefix + "/" + entityId + "/" + storedFileName;
    }

    private UUID getTenantId() {
        String tenantIdStr = TenantContextHolder.getTenantId();
        if (tenantIdStr == null) {
            throw new IllegalArgumentException("Tenant context is missing");
        }
        return UUID.fromString(tenantIdStr);
    }
}
