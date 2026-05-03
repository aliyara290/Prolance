package com.dxc.crmservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.crmservice.domain.model.valueobject.ClientStatus;
import com.dxc.crmservice.domain.model.valueobject.ClientType;
import com.dxc.crmservice.domain.model.valueobject.Source;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE clients SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class ClientEntity extends BaseAuditingEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String industry;
    private String website;
    @Column(nullable = false)
    private String phone;

    @Embedded
    private AddressEmbeddable address;

    @Enumerated(EnumType.STRING)
    private ClientStatus status;

    @Enumerated(EnumType.STRING)
    private ClientType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Source source;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
