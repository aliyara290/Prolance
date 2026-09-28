package com.dxc.billingservice.application.dto.req;

import com.dxc.billingservice.domain.model.valueobject.BillingType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateInvoiceRequest(
        @NotNull UUID projectId,
        @NotNull UUID clientId,
        @NotNull BillingType billingType,
        @NotNull LocalDate issueDate,
        @NotNull LocalDate dueDate,
        LocalDate periodStartDate,
        LocalDate periodEndDate,
        BigDecimal taxRate,
        String notes
) {}
