package com.dxc.crmservice.infrastructure.adapter.in.rest.controller;

import com.dxc.crmservice.application.dto.lead.req.CreateLeadRequest;
import com.dxc.crmservice.application.dto.lead.req.UpdateLeadRequest;
import com.dxc.crmservice.application.dto.lead.res.LeadResponse;
import com.dxc.crmservice.application.port.in.LeadUseCase;
import com.dxc.crmservice.infrastructure.adapter.in.rest.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {

    private final LeadUseCase leadUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponse>> createLead(@Valid @RequestBody CreateLeadRequest request) {
        LeadResponse leadResponse = leadUseCase.createLead(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(leadResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLead(@PathVariable UUID id, @Valid @RequestBody UpdateLeadRequest request) {
        LeadResponse leadResponse = leadUseCase.updateLead(id, request);
        return ResponseEntity.ok(ApiResponse.success(leadResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable UUID id) {
        leadUseCase.deleteLead(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> getLead(@PathVariable UUID id) {
        LeadResponse leadResponse = leadUseCase.getLead(id);
        return ResponseEntity.ok(ApiResponse.success(leadResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LeadResponse>>> getAllLeads(Pageable pageable) {
        Page<LeadResponse> leads = leadUseCase.getAllLeads(pageable);
        return ResponseEntity.ok(ApiResponse.success(leads));
    }

}
