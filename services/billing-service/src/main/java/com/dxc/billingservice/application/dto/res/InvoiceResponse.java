package com.dxc.billingservice.application.dto.res;

import com.dxc.billingservice.domain.model.valueobject.BillingType;
import com.dxc.billingservice.domain.model.valueobject.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        UUID tenantId,
        UUID projectId,
        UUID clientId,
        String invoiceNumber,
        InvoiceStatus status,
        BillingType billingType,
        LocalDate issueDate,
        LocalDate dueDate,
        LocalDate periodStartDate,
        LocalDate periodEndDate,
        BigDecimal subtotal,
        BigDecimal taxRate,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String currency,
        String notes,
        UUID attachmentId,
        List<InvoiceLineItemResponse> lineItems,
        UUID createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
