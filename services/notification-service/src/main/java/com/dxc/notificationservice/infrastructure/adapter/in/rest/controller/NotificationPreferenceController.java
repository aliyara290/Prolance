package com.dxc.notificationservice.infrastructure.adapter.in.rest.controller;

import com.dxc.notificationservice.application.dto.notification.req.UpdatePreferenceRequest;
import com.dxc.notificationservice.application.dto.preference.res.NotificationPreferenceResponse;
import com.dxc.notificationservice.application.port.in.NotificationPreferenceUseCase;
import com.dxc.notificationservice.infrastructure.adapter.in.rest.response.ApiResponse;
import com.dxc.notificationservice.infrastructure.config.TenantContextHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceUseCase preferenceUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationPreferenceResponse>>> getPreferences() {
        UUID userId = TenantContextHolder.getUserId();
        return ResponseEntity.ok(ApiResponse.success(preferenceUseCase.getPreferences(userId)));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<List<NotificationPreferenceResponse>>> updatePreferences(
            @Valid @RequestBody List<UpdatePreferenceRequest> requests
    ) {
        UUID userId = TenantContextHolder.getUserId();
        return ResponseEntity.ok(ApiResponse.success(preferenceUseCase.updatePreferences(userId, requests)));
    }
}
