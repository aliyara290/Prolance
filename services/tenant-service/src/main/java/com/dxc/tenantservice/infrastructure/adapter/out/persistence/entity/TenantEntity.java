package com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.tenantservice.domain.model.enums.TenantIndustry;
import com.dxc.tenantservice.domain.model.enums.TenantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantEntity {
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

    private TenantIndustry industry;

    @Column(nullable = false)
    private TenantStatus status;

    @Column(name = "created_at")
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "settings_id")
    private TenantSettingsEntity settings;
}
