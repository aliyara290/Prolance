package com.dxc.projectservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.projectservice.domain.model.valueobject.ResourceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "projects_resources")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResourceEntity extends BaseAuditingEntity {
    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false)
    private String name;
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceType type;

    @Column(nullable = false)
    private String url;

}
