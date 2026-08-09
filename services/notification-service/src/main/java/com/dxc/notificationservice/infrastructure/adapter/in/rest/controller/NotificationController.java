package com.dxc.notificationservice.infrastructure.adapter.in.rest.controller;

import com.dxc.notificationservice.application.dto.notification.res.NotificationResponse;
import com.dxc.notificationservice.application.dto.notification.res.UnreadCountResponse;
import com.dxc.notificationservice.application.port.in.NotificationUseCase;
import com.dxc.notificationservice.domain.model.valueobject.NotificationCategory;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.domain.model.valueobject.ReadStatus;
import com.dxc.notificationservice.infrastructure.adapter.in.rest.response.ApiResponse;
import com.dxc.notificationservice.infrastructure.adapter.in.rest.response.PageMeta;
import com.dxc.notificationservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationUseCase notificationUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            @RequestParam(name = "type", required = false) NotificationType type,
            @RequestParam(name = "readStatus", required = false) ReadStatus readStatus,
            @RequestParam(name = "category", required = false) NotificationCategory category,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        UUID userId = TenantContextHolder.getUserId();
        
        Page<NotificationResponse> resultPage = notificationUseCase.getNotifications(
                userId, type, readStatus, category,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        return ResponseEntity.ok(ApiResponse.success(
                resultPage.getContent(),
                PageMeta.from(resultPage)
        ));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount() {
        UUID userId = TenantContextHolder.getUserId();
        long count = notificationUseCase.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(new UnreadCountResponse(count)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable(name = "id") UUID id) {
        UUID userId = TenantContextHolder.getUserId();
        notificationUseCase.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        UUID userId = TenantContextHolder.getUserId();
        notificationUseCase.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<Void>> archive(@PathVariable(name = "id") UUID id) {
        UUID userId = TenantContextHolder.getUserId();
        notificationUseCase.archive(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable(name = "id") UUID id) {
        UUID userId = TenantContextHolder.getUserId();
        notificationUseCase.delete(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
