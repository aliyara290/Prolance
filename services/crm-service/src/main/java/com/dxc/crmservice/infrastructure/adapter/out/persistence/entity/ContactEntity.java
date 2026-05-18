package com.dxc.crmservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.crmservice.domain.model.valueobject.InfluenceLevel;
import com.dxc.crmservice.domain.model.valueobject.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE contacts SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class ContactEntity extends BaseAuditingEntity {
    @Id
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(nullable = false)
    private String email;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "influence_level")
    private InfluenceLevel influenceLevel;

    @Column(name = "is_primary")
    private boolean primary;

    @Column(name = "last_contacted_at")
    private LocalDateTime lastContactedAt;
    private String notes;

    private String department;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "secondary_email")
    private String secondaryEmail;

    @Embedded
    private AddressEmbeddable address;

    @Column(length = 2000)
    private String description;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
