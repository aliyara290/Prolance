package com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.tenantservice.domain.model.valueobject.TenantIndustry;
import com.dxc.tenantservice.domain.model.valueobject.TenantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE tenants SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class TenantEntity extends BaseAuditingEntity {
    @Id
    private UUID id;

    @Column(name = "keycloak_group_id", nullable = false, unique = true)
    private UUID keycloakGroupId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;
    private String website;
    private int size;

    @Column(name = "founded_date")
    private LocalDate foundedDate;

    @Column(length = 1000)
    private String description;

    private String logo;

    @Embedded
    private AddressEmbeddable address;

    @Column(name = "industry", nullable = false)
    @Enumerated(EnumType.STRING)
    private TenantIndustry industry;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TenantStatus status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "settings_id")
    private TenantSettingsEntity settings;
}
