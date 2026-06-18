package com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.tenantservice.domain.model.valueobject.Language;
import com.dxc.tenantservice.domain.model.valueobject.Theme;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tenant_user_preferences")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantUserPreferenceEntity extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "userPreference")
    private TenantUserEntity tenantUserEntity;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    private Language language;

    private String timezone;

    @Enumerated(EnumType.STRING)
    private Theme theme;
}
