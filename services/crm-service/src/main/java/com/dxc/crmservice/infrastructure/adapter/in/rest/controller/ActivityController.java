package com.dxc.crmservice.infrastructure.adapter.in.rest.controller;

import com.dxc.crmservice.application.dto.activity.req.CreateActivityRequest;
import com.dxc.crmservice.application.dto.activity.req.UpdateActivityRequest;
import com.dxc.crmservice.application.dto.activity.res.ActivityResponse;
import com.dxc.crmservice.application.port.in.ActivityUseCase;
import com.dxc.crmservice.infrastructure.adapter.in.rest.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityUseCase activityUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ActivityResponse> createActivity(@Valid @RequestBody CreateActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(activityUseCase.createActivity(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ActivityResponse> updateActivity(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateActivityRequest request) {
        return ResponseEntity.ok(activityUseCase.updateActivity(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES', 'USER')")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(activityUseCase.getActivity(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES', 'USER')")
    public ResponseEntity<Page<ActivityResponse>> getAllActivities(Pageable pageable) {
        return ResponseEntity.ok(activityUseCase.getAllActivities(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable("id") UUID id) {
        activityUseCase.deleteActivity(id);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ActivityResponse> completeActivity(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(activityUseCase.completeActivity(id));
    }

    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ActivityResponse> rescheduleActivity(
            @PathVariable("id") UUID id,
            @RequestParam("newDate") LocalDateTime newDate) {
        return ResponseEntity.ok(activityUseCase.rescheduleActivity(id, newDate));
    }
}
