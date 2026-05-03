package com.dxc.crmservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.crmservice.domain.model.valueobject.LeadStatus;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Source;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "leads")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE leads SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
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
    private Priority priority;

    @Column(name = "assigned_to")
    private UUID assignedTo;

    @Column(name = "first_contacted_at")
    private LocalDateTime firstContactedAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "unqualified_reason")
    private String unqualifiedReason;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
