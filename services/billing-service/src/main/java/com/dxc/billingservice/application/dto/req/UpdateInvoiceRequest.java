package com.dxc.billingservice.application.dto.req;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateInvoiceRequest(
        LocalDate issueDate,
        LocalDate dueDate,
        LocalDate periodStartDate,
        LocalDate periodEndDate,
        BigDecimal taxRate,
        String notes
) {}
