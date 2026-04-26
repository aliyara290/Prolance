package com.dxc.crmservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.crmservice.domain.model.valueobject.LeadPriority;
import com.dxc.crmservice.domain.model.valueobject.LeadStatus;
import com.dxc.crmservice.domain.model.valueobject.Source;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "leads")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadEntity extends BaseAuditingEntity {
    @Id
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "contact_id")
    private UUID contactId;

    @Column(name = "title", nullable = false)
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Source source;

    @Enumerated(EnumType.STRING)
    private LeadStatus status;

    private int score;

    @Enumerated(EnumType.STRING)
    private LeadPriority priority;

    @Column(name = "assigned_to")
    private UUID assignedTo;

    @Column(name = "first_contacted_at")
    private LocalDateTime firstContactedAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "unqualified_reason")
    private String unqualifiedReason;
}
