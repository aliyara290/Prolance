package com.dxc.crmservice.infrastructure.adapter.in.rest.controller;

import com.dxc.crmservice.application.dto.opportunity.req.CreateOpportunityRequest;
import com.dxc.crmservice.application.dto.opportunity.req.UpdateOpportunityRequest;
import com.dxc.crmservice.application.dto.opportunity.res.OpportunityResponse;
import com.dxc.crmservice.application.port.in.OpportunityUseCase;
import com.dxc.crmservice.domain.model.valueobject.Stage;
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

@RestController
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityUseCase opportunityUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiResponse<OpportunityResponse>> createOpportunity(@Valid @RequestBody CreateOpportunityRequest request) {
        OpportunityResponse response = opportunityUseCase.createOpportunity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiResponse<OpportunityResponse>> updateOpportunity(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateOpportunityRequest request) {
        OpportunityResponse response = opportunityUseCase.updateOpportunity(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES', 'USER')")
    public ResponseEntity<ApiResponse<OpportunityResponse>> getOpportunity(@PathVariable("id") UUID id) {
        OpportunityResponse response = opportunityUseCase.getOpportunity(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES', 'USER')")
    public ResponseEntity<ApiResponse<List<OpportunityResponse>>> getAllOpportunities(Pageable pageable) {
        Page<OpportunityResponse> opportunities = opportunityUseCase.getAllOpportunities(pageable);

        PageMeta meta = PageMeta.builder()
                .page(opportunities.getNumber())
                .size(opportunities.getSize())
                .hasPrevious(opportunities.hasPrevious())
                .hasNext(opportunities.hasNext())
                .totalPages(opportunities.getTotalPages())
                .totalElements(opportunities.getTotalElements())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(opportunities.getContent(), meta));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOpportunity(@PathVariable("id") UUID id) {
        opportunityUseCase.deleteOpportunity(id);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/stage")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiResponse<OpportunityResponse>> moveStage(
            @PathVariable("id") UUID id,
            @RequestParam Stage stage) {
        OpportunityResponse response = opportunityUseCase.moveStage(id, stage);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/won")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiResponse<OpportunityResponse>> markAsWon(@PathVariable("id") UUID id) {
        OpportunityResponse response = opportunityUseCase.markAsWon(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/lost")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiResponse<OpportunityResponse>> markAsLost(
            @PathVariable("id") UUID id,
            @RequestParam("reason") String reason) {
        OpportunityResponse response = opportunityUseCase.markAsLost(id, reason);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }
}
