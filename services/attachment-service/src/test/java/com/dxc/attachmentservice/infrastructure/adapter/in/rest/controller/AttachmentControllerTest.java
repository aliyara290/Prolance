package com.dxc.attachmentservice.infrastructure.adapter.in.rest.controller;

import com.dxc.attachmentservice.application.dto.req.PresignedUploadRequest;
import com.dxc.attachmentservice.application.dto.res.AttachmentResponse;
import com.dxc.attachmentservice.application.dto.res.AttachmentSummaryResponse;
import com.dxc.attachmentservice.application.dto.res.PresignedUrlResponse;
import com.dxc.attachmentservice.application.port.in.AttachmentUseCase;
import com.dxc.attachmentservice.domain.exception.AttachmentNotFoundException;
import com.dxc.attachmentservice.domain.exception.InvalidAttachmentException;
import com.dxc.attachmentservice.domain.model.valueobject.EntityType;
import com.dxc.attachmentservice.infrastructure.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttachmentController.class)
@Import(SecurityConfig.class)
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttachmentUseCase attachmentService;

    private static final UUID ATTACHMENT_ID = UUID.randomUUID();
    private static final UUID ENTITY_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    @WithMockUser
    @DisplayName("POST /upload should return 201 with attachment response")
    void upload_shouldReturn201() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "content".getBytes());

        AttachmentResponse response = buildAttachmentResponse();
        when(attachmentService.upload(any(), eq(EntityType.TASK), eq(ENTITY_ID)))
                .thenReturn(response);

        mockMvc.perform(multipart("/api/v1/attachments/upload")
                        .file(file)
                        .param("entityType", "TASK")
                        .param("entityId", ENTITY_ID.toString())
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.originalFileName").value("test.pdf"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /presigned-upload should return presigned URL")
    void generatePresignedUploadUrl_shouldReturn200() throws Exception {
        PresignedUrlResponse urlResponse = new PresignedUrlResponse(
                "https://s3.amazonaws.com/presigned", "key", Instant.now().plusSeconds(900));

        when(attachmentService.generatePresignedUploadUrl(any(PresignedUploadRequest.class)))
                .thenReturn(urlResponse);

        PresignedUploadRequest request = new PresignedUploadRequest(
                EntityType.PROJECT, ENTITY_ID, "doc.pdf", "application/pdf");

        mockMvc.perform(post("/api/v1/attachments/presigned-upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.url").value("https://s3.amazonaws.com/presigned"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /{id} should return attachment metadata")
    void getAttachment_shouldReturn200() throws Exception {
        AttachmentResponse response = buildAttachmentResponse();
        when(attachmentService.getAttachment(ATTACHMENT_ID)).thenReturn(response);

        mockMvc.perform(get("/api/v1/attachments/" + ATTACHMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ATTACHMENT_ID.toString()));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /{id} should return 404 when not found")
    void getAttachment_shouldReturn404WhenNotFound() throws Exception {
        when(attachmentService.getAttachment(ATTACHMENT_ID))
                .thenThrow(new AttachmentNotFoundException("Not found: " + ATTACHMENT_ID));

        mockMvc.perform(get("/api/v1/attachments/" + ATTACHMENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("ATTACHMENT_NOT_FOUND"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /entity/{type}/{id} should return attachment list")
    void getAttachmentsByEntity_shouldReturn200() throws Exception {
        AttachmentSummaryResponse summary = new AttachmentSummaryResponse(
                ATTACHMENT_ID, EntityType.TASK, ENTITY_ID,
                "test.pdf", "application/pdf", 1024,
                USER_ID, LocalDateTime.now());

        when(attachmentService.getAttachmentsByEntity(EntityType.TASK, ENTITY_ID))
                .thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/attachments/entity/TASK/" + ENTITY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].originalFileName").value("test.pdf"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /{id}/download should return presigned download URL")
    void getDownloadUrl_shouldReturn200() throws Exception {
        PresignedUrlResponse urlResponse = new PresignedUrlResponse(
                "https://s3.amazonaws.com/download", "key", Instant.now().plusSeconds(900));

        when(attachmentService.getDownloadUrl(ATTACHMENT_ID)).thenReturn(urlResponse);

        mockMvc.perform(get("/api/v1/attachments/" + ATTACHMENT_ID + "/download"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.url").value("https://s3.amazonaws.com/download"));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /{id} should return 204")
    void deleteAttachment_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/attachments/" + ATTACHMENT_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /upload should return 400 for invalid attachment")
    void upload_shouldReturn400ForInvalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "malware.exe", "application/pdf", "content".getBytes());

        when(attachmentService.upload(any(), eq(EntityType.TASK), eq(ENTITY_ID)))
                .thenThrow(new InvalidAttachmentException("Executable files are not allowed"));

        mockMvc.perform(multipart("/api/v1/attachments/upload")
                        .file(file)
                        .param("entityType", "TASK")
                        .param("entityId", ENTITY_ID.toString())
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_ATTACHMENT"));
    }

    @Test
    @DisplayName("Should return 401 for unauthenticated request")
    void shouldReturn401ForUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/attachments/" + ATTACHMENT_ID))
                .andExpect(status().isUnauthorized());
    }

    private AttachmentResponse buildAttachmentResponse() {
        return new AttachmentResponse(
                ATTACHMENT_ID, EntityType.TASK, ENTITY_ID,
                "test.pdf", "uuid-test.pdf",
                "task/" + ENTITY_ID + "/uuid-test.pdf",
                "test-bucket", "application/pdf", 1024,
                USER_ID, LocalDateTime.now()
        );
    }
}
