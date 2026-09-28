package com.dxc.billingservice.domain.model.entity;

import com.dxc.billingservice.domain.exception.ValidationException;
import com.dxc.billingservice.domain.model.valueobject.LineItemUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class InvoiceLineItem {

    private final UUID id;
    private final UUID tenantId;
    private final UUID invoiceId;
    private UUID userId;
    private String description;
    private BigDecimal quantity;
    private LineItemUnit unit;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private int displayOrder;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InvoiceLineItem create(
            UUID tenantId,
            UUID invoiceId,
            UUID userId,
            String description,
            BigDecimal quantity,
            LineItemUnit unit,
            BigDecimal unitPrice,
            int displayOrder
    ) {
        validateRequired(tenantId, "Tenant ID is required");
        validateRequired(invoiceId, "Invoice ID is required");
        validateRequired(description, "Description is required");
        validateRequired(quantity, "Quantity is required");
        validateRequired(unit, "Unit is required");
        validateRequired(unitPrice, "Unit price is required");

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Quantity must be positive");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Unit price cannot be negative");
        }

        BigDecimal total = quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);

        return InvoiceLineItem.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .invoiceId(invoiceId)
                .userId(userId)
                .description(description)
                .quantity(quantity)
                .unit(unit)
                .unitPrice(unitPrice)
                .lineTotal(total)
                .displayOrder(displayOrder)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void update(String description, BigDecimal quantity, LineItemUnit unit, BigDecimal unitPrice, Integer displayOrder) {
        if (description != null && !description.isBlank()) this.description = description;
        if (unit != null) this.unit = unit;
        if (displayOrder != null) this.displayOrder = displayOrder;

        if (quantity != null) {
            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Quantity must be positive");
            }
            this.quantity = quantity;
        }
        if (unitPrice != null) {
            if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Unit price cannot be negative");
            }
            this.unitPrice = unitPrice;
        }

        recalculateTotal();
        touch();
    }

    private void recalculateTotal() {
        this.lineTotal = this.quantity.multiply(this.unitPrice).setScale(2, RoundingMode.HALF_UP);
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    private static void validateRequired(Object value, String message) {
        if (value == null) throw new ValidationException(message);
        if (value instanceof String s && s.isBlank()) throw new ValidationException(message);
    }
}
