package com.dxc.crmservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.crmservice.domain.model.valueobject.Stage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "opportunities")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityEntity extends BaseAuditingEntity {
    @Id
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    private String title;
    private String description;

    @Column(name = "estimated_budget", nullable = false)
    private Double estimatedBudget;

    @Column(name = "expected_revenue")
    private Double expectedRevenue;

    private int probability;

    @Column(name = "expected_start_date")
    private LocalDateTime expectedStartDate;

    @Column(name = "expected_end_date")
    private LocalDateTime expectedEndDate;

    @Column(name = "closing_date")
    private LocalDateTime closingDate;

    @Enumerated(EnumType.STRING)
    private Stage stage;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "next_follow_up_at")
    private LocalDateTime nextFollowUpAt;

    @Column(name = "lost_reason", nullable = false)
    private String lostReason;
}
