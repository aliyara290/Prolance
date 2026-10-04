package com.dxc.tenantservice.infrastructure.adapter.out.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import com.dxc.tenantservice.infrastructure.adapter.in.rest.response.ApiResponse;
import java.util.Map;

import com.dxc.tenantservice.infrastructure.adapter.out.feign.config.FeignAuthInterceptor;

@FeignClient(name = "attachment-service", configuration = FeignAuthInterceptor.class)
public interface AttachmentClient {

    @PostMapping(value = "/api/v1/attachments/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<Map<String, Object>> uploadAttachment(
            @RequestPart("file") MultipartFile file,
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") UUID entityId
    );
}
