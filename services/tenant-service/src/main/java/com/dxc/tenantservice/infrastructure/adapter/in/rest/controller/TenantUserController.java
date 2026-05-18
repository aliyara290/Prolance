package com.dxc.tenantservice.infrastructure.adapter.in.rest.controller;

import com.dxc.tenantservice.application.dto.user.req.ChangeUserRoleReqDTO;
import com.dxc.tenantservice.application.dto.user.req.CreateUserReqDTO;
import com.dxc.tenantservice.application.dto.user.req.UpdateUserReqDTO;
import com.dxc.tenantservice.application.dto.user.res.UserResDTO;
import com.dxc.tenantservice.application.port.in.TenantUserUseCase;
import com.dxc.tenantservice.infrastructure.adapter.in.rest.response.ApiResponse;
import com.dxc.tenantservice.infrastructure.adapter.in.rest.response.PageMeta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants/users")
public class TenantUserController {

    private final TenantUserUseCase tenantUserUseCase;

    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @PostMapping
    public ResponseEntity<ApiResponse<UserResDTO>> inviteUser(
            @Valid @RequestBody CreateUserReqDTO request
    ) {
        log.info("POST /api/v1/tenants/users — creating user: email={}", request.getEmail());
        UserResDTO response = tenantUserUseCase.inviteUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResDTO>> updateUser(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UpdateUserReqDTO request
    ) {
        log.info("PUT /api/v1/tenants/users/{} — updating user", userId);
        UserResDTO response = tenantUserUseCase.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @DeleteMapping("/{userId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable("userId") UUID userId) {
        log.info("DELETE /api/v1/tenants/users/{} — deactivating user", userId);
        tenantUserUseCase.deactivateUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @PostMapping("{userId}/activate")
    public ResponseEntity<ApiResponse<Void>> reactivateUser(@PathVariable("userId") UUID userId) {
        log.info("POST /api/v1/tenants/users/{} — reactivating user", userId);
        tenantUserUseCase.activateUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'MEMBER', 'VIEWER', 'ACCOUNTANT', 'SALES')")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResDTO>> getUser(@PathVariable("userId") UUID userId) {
        log.info("GET /api/v1/tenants/users/{}", userId);
        UserResDTO response = tenantUserUseCase.getUser(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'VIEWER', 'ACCOUNTANT', 'SALES')")
    @GetMapping("/byKeycloakId")
    public ResponseEntity<ApiResponse<UserResDTO>> getUserByKeycloakId() {
        UserResDTO response = tenantUserUseCase.getUserByKeycloakId();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'VIEWER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResDTO>>> getAllUsers(Pageable pageable) {
        log.info("GET /api/v1/tenants/users");
        Page<UserResDTO> users = tenantUserUseCase.getAllUsers(pageable);

        PageMeta meta = PageMeta.builder()
                .totalPages(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .page(users.getNumber())
                .size(users.getSize())
                .hasNext(users.hasNext())
                .hasPrevious(users.hasPrevious())
                .build();
        return ResponseEntity.ok(ApiResponse.success(users.getContent(), meta));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @PostMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<UserResDTO>> addUserRole(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody ChangeUserRoleReqDTO request
    ) {
        log.info("POST /api/v1/tenants/users/{}/roles — adding role: {}", userId, request.getRole());
        UserResDTO response = tenantUserUseCase.addUserRole(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @DeleteMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<UserResDTO>> removeUserRole(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody ChangeUserRoleReqDTO request
    ) {
        log.info("DELETE /api/v1/tenants/users/{}/roles — removing role: {}", userId, request.getRole());
        UserResDTO response = tenantUserUseCase.removeUserRole(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
