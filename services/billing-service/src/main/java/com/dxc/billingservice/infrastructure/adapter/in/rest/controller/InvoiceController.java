package com.dxc.billingservice.infrastructure.adapter.in.rest.controller;

import com.dxc.billingservice.application.dto.req.*;
import com.dxc.billingservice.application.dto.res.InvoiceResponse;
import com.dxc.billingservice.application.dto.res.InvoiceSummaryResponse;
import com.dxc.billingservice.application.port.in.InvoiceGenerationUseCase;
import com.dxc.billingservice.application.port.in.InvoiceUseCase;
import com.dxc.billingservice.infrastructure.adapter.in.rest.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceUseCase invoiceUseCase;
    private final InvoiceGenerationUseCase invoiceGenerationUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceResponse response = invoiceUseCase.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<InvoiceResponse>> generateInvoice(
            @Valid @RequestBody GenerateInvoiceRequest request) {
        InvoiceResponse response = invoiceGenerationUseCase.generateFromTimeEntries(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoice(@PathVariable UUID invoiceId) {
        InvoiceResponse response = invoiceUseCase.getInvoice(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<InvoiceSummaryResponse>>> getAllInvoices(Pageable pageable) {
        Page<InvoiceSummaryResponse> response = invoiceUseCase.getAllInvoices(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<Page<InvoiceSummaryResponse>>> getInvoicesByProject(
            @PathVariable UUID projectId, Pageable pageable) {
        Page<InvoiceSummaryResponse> response = invoiceUseCase.getInvoicesByProject(projectId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<Page<InvoiceSummaryResponse>>> getInvoicesByClient(
            @PathVariable UUID clientId, Pageable pageable) {
        Page<InvoiceSummaryResponse> response = invoiceUseCase.getInvoicesByClient(clientId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> updateInvoice(
            @PathVariable UUID invoiceId, @Valid @RequestBody UpdateInvoiceRequest request) {
        InvoiceResponse response = invoiceUseCase.updateInvoice(invoiceId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{invoiceId}/line-items")
    public ResponseEntity<ApiResponse<InvoiceResponse>> addLineItem(
            @PathVariable UUID invoiceId, @Valid @RequestBody AddLineItemRequest request) {
        InvoiceResponse response = invoiceUseCase.addLineItem(invoiceId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{invoiceId}/line-items/{lineItemId}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> updateLineItem(
            @PathVariable UUID invoiceId, @PathVariable UUID lineItemId,
            @Valid @RequestBody UpdateLineItemRequest request) {
        InvoiceResponse response = invoiceUseCase.updateLineItem(invoiceId, lineItemId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{invoiceId}/line-items/{lineItemId}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> removeLineItem(
            @PathVariable UUID invoiceId, @PathVariable UUID lineItemId) {
        InvoiceResponse response = invoiceUseCase.removeLineItem(invoiceId, lineItemId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{invoiceId}/send")
    public ResponseEntity<ApiResponse<InvoiceResponse>> sendInvoice(@PathVariable UUID invoiceId) {
        InvoiceResponse response = invoiceUseCase.sendInvoice(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{invoiceId}/pay")
    public ResponseEntity<ApiResponse<InvoiceResponse>> markPaid(
            @PathVariable UUID invoiceId, @RequestParam(required = false) String comment) {
        InvoiceResponse response = invoiceUseCase.markPaid(invoiceId, comment);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{invoiceId}/partial-pay")
    public ResponseEntity<ApiResponse<InvoiceResponse>> markPartiallyPaid(
            @PathVariable UUID invoiceId, @RequestParam(required = false) String comment) {
        InvoiceResponse response = invoiceUseCase.markPartiallyPaid(invoiceId, comment);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{invoiceId}/cancel")
    public ResponseEntity<ApiResponse<InvoiceResponse>> cancelInvoice(
            @PathVariable UUID invoiceId, @RequestParam(required = false) String reason) {
        InvoiceResponse response = invoiceUseCase.cancelInvoice(invoiceId, reason);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{invoiceId}/pdf")
    public ResponseEntity<ApiResponse<InvoiceResponse>> generatePdf(@PathVariable UUID invoiceId) {
        InvoiceResponse response = invoiceUseCase.generateAndAttachPdf(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable UUID invoiceId) {
        invoiceUseCase.deleteInvoice(invoiceId);
        return ResponseEntity.noContent().build();
    }
}
