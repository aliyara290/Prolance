package com.dxc.projectservice.infrastructure.adapter.in.rest.controller;

import com.dxc.projectservice.application.dto.member.req.AddMemberRequest;
import com.dxc.projectservice.application.dto.member.req.UpdateMemberRoleRequest;
import com.dxc.projectservice.application.dto.member.res.MemberResponse;
import com.dxc.projectservice.application.port.in.MemberUseCase;
import com.dxc.projectservice.infrastructure.adapter.in.rest.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMemberRole(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UpdateMemberRoleRequest request) {
        MemberResponse response = memberUseCase.updateMemberRole(projectId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
