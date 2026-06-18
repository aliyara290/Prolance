package com.dxc.projectservice.infrastructure.adapter.in.rest.controller;

import com.dxc.projectservice.application.dto.milestones.res.MilestoneResponse;
import com.dxc.projectservice.application.dto.milestones.res.MilestoneStatisticsResponse;
import com.dxc.projectservice.application.port.in.MilestoneUseCase;
import com.dxc.projectservice.domain.model.valueobject.MilestoneStatus;
import com.dxc.projectservice.infrastructure.adapter.in.rest.response.ApiResponse;
import com.dxc.projectservice.infrastructure.adapter.in.rest.response.PageMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/milestones")
@RequiredArgsConstructor
public class TenantMilestoneController {

    private final MilestoneUseCase milestoneUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MilestoneResponse>>> getAllMilestones(Pageable pageable) {
        Page<MilestoneResponse> response = milestoneUseCase.getAllTenantMilestones(pageable);
        return ResponseEntity.ok(ApiResponse.success(response.getContent(), buildPageMeta(response)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<MilestoneResponse>>> getMilestonesByStatus(
            @PathVariable MilestoneStatus status,
            Pageable pageable) {
        Page<MilestoneResponse> response = milestoneUseCase.getTenantMilestonesByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response.getContent(), buildPageMeta(response)));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<MilestoneResponse>>> getUpcomingMilestones(Pageable pageable) {
        Page<MilestoneResponse> response = milestoneUseCase.getUpcomingMilestones(pageable);
        return ResponseEntity.ok(ApiResponse.success(response.getContent(), buildPageMeta(response)));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<MilestoneResponse>>> getOverdueMilestones(Pageable pageable) {
        Page<MilestoneResponse> response = milestoneUseCase.getOverdueMilestones(pageable);
        return ResponseEntity.ok(ApiResponse.success(response.getContent(), buildPageMeta(response)));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<MilestoneStatisticsResponse>> getMilestoneStatistics() {
        MilestoneStatisticsResponse response = milestoneUseCase.getMilestoneStatistics();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private PageMeta buildPageMeta(Page<?> response) {
        return PageMeta.builder()
                .totalPages(response.getTotalPages())
                .totalElements(response.getTotalElements())
                .size(response.getSize())
                .hasNext(response.hasNext())
                .hasPrevious(response.hasPrevious())
                .page(response.getNumber())
                .build();
    }
}
