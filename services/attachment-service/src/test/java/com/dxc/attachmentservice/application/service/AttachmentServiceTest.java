package com.dxc.attachmentservice.application.service;

import com.dxc.attachmentservice.application.dto.req.PresignedUploadRequest;
import com.dxc.attachmentservice.application.dto.res.AttachmentResponse;
import com.dxc.attachmentservice.application.dto.res.AttachmentSummaryResponse;
import com.dxc.attachmentservice.application.dto.res.PresignedUrlResponse;
import com.dxc.attachmentservice.application.mapper.AttachmentApplicationMapper;
import com.dxc.attachmentservice.application.port.out.AttachmentRepository;
import com.dxc.attachmentservice.application.port.out.StoragePort;
import com.dxc.attachmentservice.domain.exception.AttachmentNotFoundException;
import com.dxc.attachmentservice.domain.model.entity.Attachment;
import com.dxc.attachmentservice.domain.model.valueobject.EntityType;
import com.dxc.attachmentservice.infrastructure.config.AwsS3Properties;
import com.dxc.attachmentservice.infrastructure.config.TenantContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private StoragePort storagePort;

    @Mock
    private FileValidator fileValidator;

    @Mock
    private AttachmentApplicationMapper mapper;

    @Mock
    private AwsS3Properties s3Properties;

    @InjectMocks
    private AttachmentService attachmentService;

    private static final UUID TENANT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ENTITY_ID = UUID.randomUUID();
    private static final UUID ATTACHMENT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContextHolder.setTenantId(TENANT_ID.toString());
        TenantContextHolder.setUserId(USER_ID);
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
        TenantContextHolder.clearUserId();
    }

    @Test
    @DisplayName("Should upload file successfully")
    void upload_shouldUploadFileAndSaveMetadata() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-doc.pdf", "application/pdf", "pdf content".getBytes());

        when(s3Properties.getBucketName()).thenReturn("test-bucket");
        when(attachmentRepository.save(any(Attachment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Attachment.class))).thenReturn(buildAttachmentResponse());

        AttachmentResponse result = attachmentService.upload(file, EntityType.TASK, ENTITY_ID);

        assertThat(result).isNotNull();
        verify(fileValidator).validate(file);
        verify(storagePort).upload(anyString(), any(), anyLong(), eq("application/pdf"));
        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    @DisplayName("Should generate presigned upload URL")
    void generatePresignedUploadUrl_shouldReturnUrl() {
        PresignedUploadRequest request = new PresignedUploadRequest(
                EntityType.PROJECT, ENTITY_ID, "report.pdf", "application/pdf");

        when(storagePort.generatePresignedUploadUrl(anyString(), eq("application/pdf"), any()))
                .thenReturn("https://s3.amazonaws.com/presigned-url");

        PresignedUrlResponse result = attachmentService.generatePresignedUploadUrl(request);

        assertThat(result).isNotNull();
        assertThat(result.url()).isEqualTo("https://s3.amazonaws.com/presigned-url");
        verify(fileValidator).validateContentType("application/pdf");
    }

    @Test
    @DisplayName("Should get attachment metadata")
    void getAttachment_shouldReturnMetadata() {
        Attachment attachment = buildAttachment();
        when(attachmentRepository.findById(ATTACHMENT_ID, TENANT_ID)).thenReturn(Optional.of(attachment));
        when(mapper.toResponse(attachment)).thenReturn(buildAttachmentResponse());

        AttachmentResponse result = attachmentService.getAttachment(ATTACHMENT_ID);

        assertThat(result).isNotNull();
        verify(attachmentRepository).findById(ATTACHMENT_ID, TENANT_ID);
    }

    @Test
    @DisplayName("Should throw when attachment not found")
    void getAttachment_shouldThrowWhenNotFound() {
        when(attachmentRepository.findById(ATTACHMENT_ID, TENANT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.getAttachment(ATTACHMENT_ID))
                .isInstanceOf(AttachmentNotFoundException.class)
                .hasMessageContaining(ATTACHMENT_ID.toString());
    }

    @Test
    @DisplayName("Should list attachments by entity")
    void getAttachmentsByEntity_shouldReturnList() {
        List<Attachment> attachments = List.of(buildAttachment());
        when(attachmentRepository.findByEntityTypeAndEntityId(EntityType.TASK, ENTITY_ID, TENANT_ID))
                .thenReturn(attachments);
        when(mapper.toSummaryResponseList(attachments)).thenReturn(List.of(buildSummaryResponse()));

        List<AttachmentSummaryResponse> result = attachmentService.getAttachmentsByEntity(EntityType.TASK, ENTITY_ID);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should generate presigned download URL")
    void getDownloadUrl_shouldReturnPresignedUrl() {
        Attachment attachment = buildAttachment();
        when(attachmentRepository.findById(ATTACHMENT_ID, TENANT_ID)).thenReturn(Optional.of(attachment));
        when(storagePort.generatePresignedDownloadUrl(eq(attachment.getObjectKey()), any()))
                .thenReturn("https://s3.amazonaws.com/download-url");

        PresignedUrlResponse result = attachmentService.getDownloadUrl(ATTACHMENT_ID);

        assertThat(result.url()).isEqualTo("https://s3.amazonaws.com/download-url");
    }

    @Test
    @DisplayName("Should delete attachment and S3 object")
    void deleteAttachment_shouldDeleteFromStorageAndDatabase() {
        Attachment attachment = buildAttachment();
        when(attachmentRepository.findById(ATTACHMENT_ID, TENANT_ID)).thenReturn(Optional.of(attachment));

        attachmentService.deleteAttachment(ATTACHMENT_ID);

        verify(storagePort).delete(attachment.getObjectKey());
        verify(attachmentRepository).delete(ATTACHMENT_ID, TENANT_ID);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent attachment")
    void deleteAttachment_shouldThrowWhenNotFound() {
        when(attachmentRepository.findById(ATTACHMENT_ID, TENANT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.deleteAttachment(ATTACHMENT_ID))
                .isInstanceOf(AttachmentNotFoundException.class);

        verify(storagePort, never()).delete(anyString());
    }

    private Attachment buildAttachment() {
        return Attachment.builder()
                .id(ATTACHMENT_ID)
                .tenantId(TENANT_ID)
                .entityType(EntityType.TASK)
                .entityId(ENTITY_ID)
                .originalFileName("test.pdf")
                .storedFileName("uuid-test.pdf")
                .objectKey("task/" + ENTITY_ID + "/uuid-test.pdf")
                .bucketName("test-bucket")
                .contentType("application/pdf")
                .size(1024)
                .uploadedBy(USER_ID)
                .uploadedAt(java.time.LocalDateTime.now())
                .deleted(false)
                .build();
    }

    private AttachmentResponse buildAttachmentResponse() {
        return new AttachmentResponse(
                ATTACHMENT_ID, EntityType.TASK, ENTITY_ID,
                "test.pdf", "uuid-test.pdf",
                "task/" + ENTITY_ID + "/uuid-test.pdf",
                "test-bucket", "application/pdf", 1024,
                USER_ID, java.time.LocalDateTime.now()
        );
    }

    private AttachmentSummaryResponse buildSummaryResponse() {
        return new AttachmentSummaryResponse(
                ATTACHMENT_ID, EntityType.TASK, ENTITY_ID,
                "test.pdf", "application/pdf", 1024,
                USER_ID, java.time.LocalDateTime.now()
        );
    }
}
