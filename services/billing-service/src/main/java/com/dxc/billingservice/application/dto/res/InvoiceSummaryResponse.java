package com.dxc.billingservice.application.dto.res;

import com.dxc.billingservice.domain.model.valueobject.BillingType;
import com.dxc.billingservice.domain.model.valueobject.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record InvoiceSummaryResponse(
        UUID id,
        UUID projectId,
        UUID clientId,
        String invoiceNumber,
        InvoiceStatus status,
        BillingType billingType,
        LocalDate issueDate,
        LocalDate dueDate,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime createdAt
) {}
