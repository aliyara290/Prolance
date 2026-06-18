package com.dxc.projectservice.infrastructure.adapter.in.rest.controller;

import com.dxc.projectservice.application.dto.member.req.AddMemberRequest;

import com.dxc.projectservice.application.dto.member.res.MemberResponse;
import com.dxc.projectservice.application.port.in.MemberUseCase;
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
@RequestMapping("/api/v1/projects/{projectId}/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberUseCase memberUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> addMember(
            @PathVariable("projectId") UUID projectId,
            @Valid @RequestBody AddMemberRequest request) {
        MemberResponse response = memberUseCase.addMember(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("userId") UUID userId) {
        memberUseCase.removeMember(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMember(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody com.dxc.projectservice.application.dto.member.req.UpdateMemberRequest request) {
        MemberResponse response = memberUseCase.updateMember(projectId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getMembers(
            @PathVariable("projectId") UUID projectId,
            Pageable pageable) {
        Page<MemberResponse> response = memberUseCase.getMembers(projectId, pageable);
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

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("userId") UUID userId) {
        MemberResponse response = memberUseCase.getMemberById(projectId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
