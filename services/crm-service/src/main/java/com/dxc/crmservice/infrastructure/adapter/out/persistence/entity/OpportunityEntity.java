package com.dxc.crmservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
