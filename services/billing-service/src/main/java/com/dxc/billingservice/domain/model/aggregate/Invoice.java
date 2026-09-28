package com.dxc.billingservice.domain.model.aggregate;

import com.dxc.billingservice.domain.exception.BusinessRuleException;
import com.dxc.billingservice.domain.exception.StateTransitionException;
import com.dxc.billingservice.domain.exception.ValidationException;
import com.dxc.billingservice.domain.model.entity.InvoiceLineItem;
import com.dxc.billingservice.domain.model.entity.InvoiceStatusHistory;
import com.dxc.billingservice.domain.model.event.InvoiceCreated;
import com.dxc.billingservice.domain.model.event.InvoicePaid;
import com.dxc.billingservice.domain.model.event.InvoiceSent;
import com.dxc.billingservice.domain.model.valueobject.BillingType;
import com.dxc.billingservice.domain.model.valueobject.InvoiceStatus;
import com.dxc.billingservice.domain.model.valueobject.LineItemUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Invoice extends AggregateRoot {

    private final UUID id;
    private final UUID tenantId;
    private final UUID projectId;
    private UUID clientId;

    private String invoiceNumber;
    private InvoiceStatus status;
    private BillingType billingType;

    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;

    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String currency;

    private String notes;
    private UUID attachmentId;

    private final List<InvoiceLineItem> lineItems;
    private final List<InvoiceStatusHistory> statusHistory;

    private final UUID createdBy;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Invoice create(
            UUID tenantId,
            UUID projectId,
            UUID clientId,
            String invoiceNumber,
            BillingType billingType,
            LocalDate issueDate,
            LocalDate dueDate,
            LocalDate periodStartDate,
            LocalDate periodEndDate,
            BigDecimal taxRate,
            String notes,
            UUID createdBy
    ) {
        validateRequired(tenantId, "Tenant ID is required");
        validateRequired(projectId, "Project ID is required");
        validateRequired(clientId, "Client ID is required");
        validateRequired(invoiceNumber, "Invoice number is required");
        validateRequired(billingType, "Billing type is required");
        validateRequired(issueDate, "Issue date is required");
        validateRequired(dueDate, "Due date is required");
        validateRequired(createdBy, "Created by is required");

        if (dueDate.isBefore(issueDate)) {
            throw new ValidationException("Due date cannot be before issue date");
        }

        BigDecimal effectiveTaxRate = taxRate != null ? taxRate : BigDecimal.valueOf(20);

        Invoice invoice = Invoice.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .projectId(projectId)
                .clientId(clientId)
                .invoiceNumber(invoiceNumber)
                .status(InvoiceStatus.DRAFT)
                .billingType(billingType)
                .issueDate(issueDate)
                .dueDate(dueDate)
                .periodStartDate(periodStartDate)
                .periodEndDate(periodEndDate)
                .subtotal(BigDecimal.ZERO)
                .taxRate(effectiveTaxRate)
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .currency("EUR")
                .notes(notes)
                .lineItems(new ArrayList<>())
                .statusHistory(new ArrayList<>())
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        invoice.statusHistory.add(InvoiceStatusHistory.create(
                tenantId, invoice.getId(), null, InvoiceStatus.DRAFT, createdBy, "Invoice created"));

        invoice.registerEvent(InvoiceCreated.now(
                tenantId, invoice.getId(), projectId, clientId, invoiceNumber, createdBy));

        return invoice;
    }

    public void addLineItem(
            UUID userId,
            String description,
            BigDecimal quantity,
            LineItemUnit unit,
            BigDecimal unitPrice,
            int displayOrder
    ) {
        requireDraft("Cannot add line items to a non-draft invoice");

        InvoiceLineItem item = InvoiceLineItem.create(
                tenantId, id, userId, description, quantity, unit, unitPrice, displayOrder);

        lineItems.add(item);
        recalculateTotals();
        touch();
    }

    public void removeLineItem(UUID lineItemId) {
        requireDraft("Cannot remove line items from a non-draft invoice");

        InvoiceLineItem item = lineItems.stream()
                .filter(li -> li.getId().equals(lineItemId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Line item not found"));

        lineItems.remove(item);
        recalculateTotals();
        touch();
    }

    public void updateLineItem(
            UUID lineItemId,
            String description,
            BigDecimal quantity,
            LineItemUnit unit,
            BigDecimal unitPrice,
            Integer displayOrder
    ) {
        requireDraft("Cannot update line items on a non-draft invoice");

        InvoiceLineItem item = lineItems.stream()
                .filter(li -> li.getId().equals(lineItemId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Line item not found"));

        item.update(description, quantity, unit, unitPrice, displayOrder);
        recalculateTotals();
        touch();
    }

    public void updateDetails(
            LocalDate issueDate,
            LocalDate dueDate,
            LocalDate periodStartDate,
            LocalDate periodEndDate,
            BigDecimal taxRate,
            String notes
    ) {
        requireDraft("Cannot update a non-draft invoice");

        if (issueDate != null) this.issueDate = issueDate;
        if (dueDate != null) this.dueDate = dueDate;
        if (periodStartDate != null) this.periodStartDate = periodStartDate;
        if (periodEndDate != null) this.periodEndDate = periodEndDate;
        if (notes != null) this.notes = notes;

        if (taxRate != null) {
            if (taxRate.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Tax rate cannot be negative");
            }
            this.taxRate = taxRate;
        }

        if (this.dueDate.isBefore(this.issueDate)) {
            throw new ValidationException("Due date cannot be before issue date");
        }

        recalculateTotals();
        touch();
    }

    public void send(UUID actionBy) {
        if (lineItems.isEmpty()) {
            throw new BusinessRuleException("Cannot send an invoice with no line items");
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Cannot send an invoice with zero or negative total");
        }
        changeStatus(InvoiceStatus.SENT, actionBy, "Invoice sent to client");
        registerEvent(InvoiceSent.now(tenantId, id, clientId, totalAmount, actionBy));
    }

    public void markPaid(UUID actionBy, String comment) {
        if (status != InvoiceStatus.SENT && status != InvoiceStatus.OVERDUE && status != InvoiceStatus.PARTIALLY_PAID) {
            throw new StateTransitionException("Invoice can only be marked paid from SENT, OVERDUE, or PARTIALLY_PAID status");
        }
        changeStatus(InvoiceStatus.PAID, actionBy, comment != null ? comment : "Invoice fully paid");
        registerEvent(InvoicePaid.now(tenantId, id, totalAmount, actionBy));
    }

    public void markPartiallyPaid(UUID actionBy, String comment) {
        if (status != InvoiceStatus.SENT && status != InvoiceStatus.OVERDUE) {
            throw new StateTransitionException("Invoice can only be partially paid from SENT or OVERDUE status");
        }
        changeStatus(InvoiceStatus.PARTIALLY_PAID, actionBy, comment != null ? comment : "Partial payment received");
    }

    public void markOverdue(UUID actionBy) {
        if (status != InvoiceStatus.SENT) {
            throw new StateTransitionException("Only sent invoices can be marked overdue");
        }
        changeStatus(InvoiceStatus.OVERDUE, actionBy, "Invoice is overdue");
    }

    public void cancel(UUID actionBy, String reason) {
        if (status == InvoiceStatus.PAID) {
            throw new StateTransitionException("Cannot cancel a paid invoice");
        }
        if (status == InvoiceStatus.CANCELLED) {
            throw new StateTransitionException("Invoice is already cancelled");
        }
        changeStatus(InvoiceStatus.CANCELLED, actionBy, reason != null ? reason : "Invoice cancelled");
    }

    public void attachPdf(UUID attachmentId) {
        validateRequired(attachmentId, "Attachment ID is required");
        this.attachmentId = attachmentId;
        touch();
    }

    private void changeStatus(InvoiceStatus newStatus, UUID actionBy, String comment) {
        InvoiceStatus oldStatus = this.status;
        this.status = newStatus;
        statusHistory.add(InvoiceStatusHistory.create(tenantId, id, oldStatus, newStatus, actionBy, comment));
        touch();
    }

    private void recalculateTotals() {
        this.subtotal = lineItems.stream()
                .map(InvoiceLineItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        this.taxAmount = this.subtotal
                .multiply(this.taxRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        this.totalAmount = this.subtotal.add(this.taxAmount).setScale(2, RoundingMode.HALF_UP);
    }

    private void requireDraft(String message) {
        if (status != InvoiceStatus.DRAFT) {
            throw new BusinessRuleException(message);
        }
    }

    public void attachDocument(UUID attachmentId) {
        if (attachmentId == null) {
            throw new ValidationException("Attachment ID cannot be null");
        }
        this.attachmentId = attachmentId;
        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    private static void validateRequired(Object value, String message) {
        if (value == null) throw new ValidationException(message);
        if (value instanceof String s && s.isBlank()) throw new ValidationException(message);
    }
}
