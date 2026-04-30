package com.dxc.crmservice.infrastructure.adapter.in.rest.controller;

import com.dxc.crmservice.application.dto.activity.req.CreateActivityRequest;
import com.dxc.crmservice.application.dto.activity.req.UpdateActivityRequest;
import com.dxc.crmservice.application.dto.activity.res.ActivityResponse;
import com.dxc.crmservice.application.port.in.ActivityUseCase;
import com.dxc.crmservice.infrastructure.adapter.in.rest.response.ApiResponse;
import com.dxc.crmservice.infrastructure.adapter.in.rest.response.PageMeta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityUseCase activityUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiResponse<ActivityResponse>> createActivity(@Valid @RequestBody CreateActivityRequest request) {
        ActivityResponse response = activityUseCase.createActivity((request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiResponse<ActivityResponse>> updateActivity(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateActivityRequest request) {
        ActivityResponse response = activityUseCase.updateActivity(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<ActivityResponse>> getActivity(@PathVariable("id") UUID id) {
        ActivityResponse response = activityUseCase.getActivity(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<List<ActivityResponse>>> getAllActivities(Pageable pageable) {
        Page<ActivityResponse> activities = activityUseCase.getAllActivities(pageable);

        PageMeta meta = PageMeta.builder()
                .page(activities.getNumber())
                .size(activities.getSize())
                .hasPrevious(activities.hasPrevious())
                .hasNext(activities.hasNext())
                .totalPages(activities.getTotalPages())
                .totalElements(activities.getTotalElements())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(activities.getContent(), meta));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable("id") UUID id) {
        activityUseCase.deleteActivity(id);

        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiResponse<ActivityResponse>> completeActivity(@PathVariable("id") UUID id) {
        ActivityResponse response = activityUseCase.completeActivity(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiResponse<ActivityResponse>> rescheduleActivity(
            @PathVariable("id") UUID id,
            @RequestParam("newDate") LocalDateTime newDate) {
        ActivityResponse response = activityUseCase.rescheduleActivity(id, newDate);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }
}
