package com.dxc.tenantservice.infrastructure.adapter.in.rest;

import com.dxc.tenantservice.application.dto.tenant.req.RegisterTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.req.UpdateTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantResDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantTokenResDTO;
import com.dxc.tenantservice.application.port.in.TenantUseCase;
import com.dxc.tenantservice.infrastructure.adapter.in.rest.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final TenantUseCase tenantUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<TenantTokenResDTO>> registerTenant(
            @Valid @RequestBody RegisterTenantReqDTO request
    ) {
        TenantTokenResDTO response = tenantUseCase.registerTenant(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PutMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantResDTO>> updateTenant(@PathVariable UUID tenantId, @Valid @RequestBody UpdateTenantReqDTO request) {
        log.info("Updating tenantId={}", tenantId);

        TenantResDTO response = tenantUseCase.updateTenant(tenantId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable UUID tenantId) {
        tenantUseCase.deleteTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantResDTO>> getTenant(@PathVariable UUID tenantId) {
        TenantResDTO response = tenantUseCase.getTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}