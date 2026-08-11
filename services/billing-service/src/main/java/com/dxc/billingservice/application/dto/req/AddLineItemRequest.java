package com.dxc.billingservice.application.dto.req;

import com.dxc.billingservice.domain.model.valueobject.LineItemUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record AddLineItemRequest(
        UUID userId,
        @NotBlank String description,
        @NotNull @Positive BigDecimal quantity,
        @NotNull LineItemUnit unit,
        @NotNull @Positive BigDecimal unitPrice,
        int displayOrder
) {}
