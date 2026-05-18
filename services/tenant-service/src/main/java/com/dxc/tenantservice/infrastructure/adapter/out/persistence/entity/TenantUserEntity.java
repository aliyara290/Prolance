package com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.tenantservice.domain.model.valueobject.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tenant_users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE tenant_users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class TenantUserEntity extends BaseAuditingEntity {
    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "keycloak_user_id", nullable = false, unique = true)
    private UUID keycloakUserId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tenant_user_role_groups",
            joinColumns = @JoinColumn(name = "tenant_user_id")
    )
    @Column(name = "keycloak_role_group_id")
    @Builder.Default
    private Set<UUID> keycloakRoleGroupIds = new HashSet<>();

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "job_title")
    private String jobTitle;

    private String department;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_preference_id")
    private TenantUserPreferenceEntity userPreference;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}