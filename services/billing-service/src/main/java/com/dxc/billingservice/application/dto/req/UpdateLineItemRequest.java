package com.dxc.billingservice.application.dto.req;

import com.dxc.billingservice.domain.model.valueobject.LineItemUnit;

import java.math.BigDecimal;

public record UpdateLineItemRequest(
        String description,
        BigDecimal quantity,
        LineItemUnit unit,
        BigDecimal unitPrice,
        Integer displayOrder
) {}
