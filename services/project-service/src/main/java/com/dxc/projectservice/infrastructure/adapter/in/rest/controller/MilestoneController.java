package com.dxc.projectservice.infrastructure.adapter.in.rest.controller;

import com.dxc.projectservice.application.dto.milestones.req.CreateMilestoneRequest;
import com.dxc.projectservice.application.dto.milestones.req.UpdateMilestoneRequest;
import com.dxc.projectservice.application.dto.milestones.res.MilestoneResponse;
import com.dxc.projectservice.application.port.in.MilestoneUseCase;
import com.dxc.projectservice.infrastructure.adapter.in.rest.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.dxc.projectservice.infrastructure.adapter.in.rest.response.PageMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/milestones")
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneUseCase milestoneUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<MilestoneResponse>> addMilestone(
            @PathVariable("projectId") UUID projectId,
            @Valid @RequestBody CreateMilestoneRequest request) {
        MilestoneResponse response = milestoneUseCase.addMilestone(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @DeleteMapping("/{milestoneId}")
    public ResponseEntity<Void> removeMilestone(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("milestoneId") UUID milestoneId) {
        milestoneUseCase.removeMilestone(projectId, milestoneId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{milestoneId}")
    public ResponseEntity<ApiResponse<MilestoneResponse>> updateMilestone(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("milestoneId") UUID milestoneId,
            @Valid @RequestBody UpdateMilestoneRequest request) {
        MilestoneResponse response = milestoneUseCase.updateMilestone(projectId, milestoneId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{milestoneId}/complete")
    public ResponseEntity<ApiResponse<MilestoneResponse>> completeMilestone(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("milestoneId") UUID milestoneId) {
        MilestoneResponse response = milestoneUseCase.completeMilestone(projectId, milestoneId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MilestoneResponse>>> getMilestones(
            @PathVariable("projectId") UUID projectId,
            Pageable pageable) {
        Page<MilestoneResponse> response = milestoneUseCase.getMilestones(projectId, pageable);
        PageMeta meta = PageMeta.builder()
                .totalPages(response.getTotalPages())
                .totalElements(response.getTotalElements())
                .size(response.getSize())
                .hasNext(response.hasNext())
                .hasPrevious(response.hasPrevious())
                .page(response.getNumber())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response.getContent(), meta));
    }

    @GetMapping("/{milestoneId}")
    public ResponseEntity<ApiResponse<MilestoneResponse>> getMilestoneById(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("milestoneId") UUID milestoneId) {
        MilestoneResponse response = milestoneUseCase.getMilestoneById(projectId, milestoneId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


}
