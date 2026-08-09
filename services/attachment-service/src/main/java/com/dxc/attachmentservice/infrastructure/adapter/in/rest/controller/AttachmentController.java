package com.dxc.attachmentservice.infrastructure.adapter.in.rest.controller;

import com.dxc.attachmentservice.application.dto.req.PresignedUploadRequest;
import com.dxc.attachmentservice.application.dto.res.AttachmentResponse;
import com.dxc.attachmentservice.application.dto.res.AttachmentSummaryResponse;
import com.dxc.attachmentservice.application.dto.res.PresignedUrlResponse;
import com.dxc.attachmentservice.application.port.in.AttachmentUseCase;
import com.dxc.attachmentservice.domain.model.valueobject.EntityType;
import com.dxc.attachmentservice.infrastructure.adapter.in.rest.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/attachments")
@Tag(name = "Attachments", description = "Attachment management endpoints")
public class AttachmentController {

    private final AttachmentUseCase attachmentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file", description = "Uploads a file and stores it in S3 with metadata in the database")
    public ResponseEntity<ApiResponse<AttachmentResponse>> upload(
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Type of entity this attachment belongs to") @RequestParam("entityType") EntityType entityType,
            @Parameter(description = "ID of the entity this attachment belongs to") @RequestParam("entityId") UUID entityId) {

        AttachmentResponse response = attachmentService.upload(file, entityType, entityId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PostMapping("/presigned-upload")
    @Operation(summary = "Generate pre-signed upload URL", description = "Returns a pre-signed S3 URL for direct client-side upload")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> generatePresignedUploadUrl(
            @Valid @RequestBody PresignedUploadRequest request) {

        PresignedUrlResponse response = attachmentService.generatePresignedUploadUrl(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{attachmentId}")
    @Operation(summary = "Get attachment metadata", description = "Returns metadata for a specific attachment")
    public ResponseEntity<ApiResponse<AttachmentResponse>> getAttachment(
            @Parameter(description = "Attachment ID") @PathVariable("attachmentId") UUID attachmentId) {

        AttachmentResponse response = attachmentService.getAttachment(attachmentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "List attachments by entity", description = "Returns all attachments belonging to a specific entity")
    public ResponseEntity<ApiResponse<List<AttachmentSummaryResponse>>> getAttachmentsByEntity(
            @Parameter(description = "Entity type") @PathVariable("entityType") EntityType entityType,
            @Parameter(description = "Entity ID") @PathVariable("entityId") UUID entityId) {

        List<AttachmentSummaryResponse> response = attachmentService.getAttachmentsByEntity(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{attachmentId}/download")
    @Operation(summary = "Get download URL", description = "Returns a pre-signed download URL for the attachment")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getDownloadUrl(
            @Parameter(description = "Attachment ID") @PathVariable("attachmentId") UUID attachmentId) {

        PresignedUrlResponse response = attachmentService.getDownloadUrl(attachmentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{attachmentId}")
    @Operation(summary = "Delete attachment", description = "Deletes both the S3 object and the metadata record")
    public ResponseEntity<Void> deleteAttachment(
            @Parameter(description = "Attachment ID") @PathVariable("attachmentId") UUID attachmentId) {

        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
