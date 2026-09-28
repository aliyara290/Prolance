package com.dxc.billingservice.infrastructure.persistence.entity;

import com.dxc.billingservice.domain.model.valueobject.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invoice_status_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceStatusHistoryJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceJpaEntity invoice;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus newStatus;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private UUID changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt;
}
