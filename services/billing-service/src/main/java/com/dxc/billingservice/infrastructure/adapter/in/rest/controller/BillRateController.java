package com.dxc.billingservice.infrastructure.adapter.in.rest.controller;

import com.dxc.billingservice.application.dto.req.CreateBillRateRequest;
import com.dxc.billingservice.application.dto.req.UpdateBillRateRequest;
import com.dxc.billingservice.application.dto.res.BillRateResponse;
import com.dxc.billingservice.application.port.in.BillRateUseCase;
import com.dxc.billingservice.infrastructure.adapter.in.rest.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/bill-rates")
public class BillRateController {

    private final BillRateUseCase billRateUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<BillRateResponse>> createBillRate(
            @Valid @RequestBody CreateBillRateRequest request) {
        BillRateResponse response = billRateUseCase.createBillRate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{billRateId}")
    public ResponseEntity<ApiResponse<BillRateResponse>> getBillRate(@PathVariable("billRateId") UUID billRateId) {
        BillRateResponse response = billRateUseCase.getBillRate(billRateId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<BillRateResponse>>> getBillRatesByProject(@PathVariable("projectId") UUID projectId) {
        List<BillRateResponse> response = billRateUseCase.getBillRatesByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/project/{projectId}/user/{userId}")
    public ResponseEntity<ApiResponse<BillRateResponse>> getBillRateByProjectAndUser(
            @PathVariable("projectId") UUID projectId, @PathVariable("userId") UUID userId) {
        BillRateResponse response = billRateUseCase.getBillRateByProjectAndUser(projectId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{billRateId}")
    public ResponseEntity<ApiResponse<BillRateResponse>> updateBillRate(
            @PathVariable("billRateId") UUID billRateId, @Valid @RequestBody UpdateBillRateRequest request) {
        BillRateResponse response = billRateUseCase.updateBillRate(billRateId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{billRateId}")
    public ResponseEntity<Void> deleteBillRate(@PathVariable("billRateId") UUID billRateId) {
        billRateUseCase.deleteBillRate(billRateId);
        return ResponseEntity.noContent().build();
    }
}
