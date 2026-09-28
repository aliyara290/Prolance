package com.dxc.billingservice.application.dto.res;

import com.dxc.billingservice.domain.model.valueobject.LineItemUnit;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceLineItemResponse(
        UUID id,
        UUID userId,
        String description,
        BigDecimal quantity,
        LineItemUnit unit,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        int displayOrder
) {}
