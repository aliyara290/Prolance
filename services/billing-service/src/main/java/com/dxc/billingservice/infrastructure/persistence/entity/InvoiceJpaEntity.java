package com.dxc.billingservice.infrastructure.persistence.entity;

import com.dxc.billingservice.domain.model.valueobject.BillingType;
import com.dxc.billingservice.domain.model.valueobject.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID projectId;

    @Column(nullable = false)
    private UUID clientId;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingType billingType;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate periodStartDate;
    private LocalDate periodEndDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private UUID attachmentId;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<InvoiceLineItemJpaEntity> lineItems = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("changedAt ASC")
    @Builder.Default
    private List<InvoiceStatusHistoryJpaEntity> statusHistory = new ArrayList<>();

    @Column(nullable = false)
    private UUID createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
