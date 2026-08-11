package com.dxc.billingservice.infrastructure.adapter.out.feign;

import com.dxc.billingservice.application.port.out.feign.AttachmentFeignPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;
import com.dxc.billingservice.infrastructure.config.FeignFormConfig;

@FeignClient(name = "attachment-service", path = "/api/v1/attachments", configuration = FeignFormConfig.class)
public interface AttachmentFeignClient {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    AttachmentFeignPort.AttachmentResponseDTO uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType,
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") UUID entityId
    );

    @GetMapping("/{attachmentId}/download")
    String getDownloadUrl(@PathVariable("attachmentId") UUID attachmentId);
}
