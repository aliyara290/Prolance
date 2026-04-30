package com.dxc.crmservice.infrastructure.adapter.in.rest.controller;

import com.dxc.crmservice.application.dto.lead.req.CreateLeadRequest;
import com.dxc.crmservice.application.dto.lead.req.UpdateLeadRequest;
import com.dxc.crmservice.application.dto.lead.res.LeadResponse;
import com.dxc.crmservice.application.dto.opportunity.res.OpportunityResponse;
import com.dxc.crmservice.application.port.in.LeadUseCase;
import com.dxc.crmservice.infrastructure.adapter.in.rest.response.ApiResponse;
import com.dxc.crmservice.infrastructure.adapter.in.rest.response.PageMeta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {

    private final LeadUseCase leadUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiResponse<LeadResponse>> createLead(@Valid @RequestBody CreateLeadRequest request) {
        LeadResponse leadResponse = leadUseCase.createLead(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(leadResponse));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLead(@PathVariable("id") UUID id, @Valid @RequestBody UpdateLeadRequest request) {
        LeadResponse leadResponse = leadUseCase.updateLead(id, request);
        return ResponseEntity.ok(ApiResponse.success(leadResponse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable("id") UUID id) {
        leadUseCase.deleteLead(id);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES', 'USER')")
    public ResponseEntity<ApiResponse<LeadResponse>> getLead(@PathVariable("id") UUID id) {
        LeadResponse leadResponse = leadUseCase.getLead(id);
        return ResponseEntity.ok(ApiResponse.success(leadResponse));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES', 'USER')")
    public ResponseEntity<ApiResponse<List<LeadResponse>>> getAllLeads(Pageable pageable) {
        Page<LeadResponse> leads = leadUseCase.getAllLeads(pageable);

        PageMeta meta = PageMeta.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .hasNext(leads.hasNext())
                .hasPrevious(leads.hasPrevious())
                .totalPages(leads.getTotalPages())
                .totalElements(leads.getTotalElements())
                .build();

        return ResponseEntity.ok(ApiResponse.success(leads.getContent(), meta));
    }

    @PostMapping("/{id}/convert")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiResponse<OpportunityResponse>> convertToOpportunity(@PathVariable("id") UUID id) {
        OpportunityResponse response = leadUseCase.qualifyAndConvert(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
