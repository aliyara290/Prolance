package com.dxc.tenantservice.infrastructure.adapter.in.rest.controller;

import com.dxc.tenantservice.application.dto.tenant.req.RegisterTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.req.UpdateTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.res.RegisterTenantResDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantRegistrationResponseDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantResDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantStatusRespDTO;
import com.dxc.tenantservice.application.port.in.TenantUseCase;
import com.dxc.tenantservice.infrastructure.adapter.in.rest.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final TenantUseCase tenantUseCase;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TenantRegistrationResponseDTO>> registerTenant(
            @Valid @RequestBody RegisterTenantReqDTO request
    ) {
        TenantRegistrationResponseDTO response = tenantUseCase.registerTenant(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/{tenantId}/status")
    public ResponseEntity<ApiResponse<TenantStatusRespDTO>> getTenantStatus(@PathVariable("tenantId") UUID tenantId) {
        TenantStatusRespDTO response = tenantUseCase.getTenantStatus(tenantId);
        log.info("Tenant status 101010101: {}", response.toString());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantResDTO>> updateTenant(@PathVariable("tenantId") UUID tenantId, @Valid @RequestBody UpdateTenantReqDTO request) {
        log.info("Updating tenantId={}", tenantId);

        TenantResDTO response = tenantUseCase.updateTenant(tenantId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable("tenantId") UUID tenantId) {
        tenantUseCase.deleteTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER', 'VIEWER', 'PROJECT_MANAGER', 'ACCOUNTANT')")
    @GetMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantResDTO>> getTenant(@PathVariable("tenantId") UUID tenantId) {
        TenantResDTO response = tenantUseCase.getTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}