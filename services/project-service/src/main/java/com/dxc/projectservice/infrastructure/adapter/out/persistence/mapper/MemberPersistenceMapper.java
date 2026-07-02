package com.dxc.projectservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.projectservice.domain.model.entity.Member;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.MemberEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberPersistenceMapper {

    public MemberEntity toEntity(Member domain) {
        if (domain == null) return null;
        
        MemberEntity entity = new MemberEntity();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setProjectId(domain.getProjectId());
        entity.setUserId(domain.getUserId());
        entity.setRole(domain.getRole());
        entity.setAllocationPercentage(domain.getAllocationPercentage());
        entity.setStatus(domain.getStatus());
        entity.setJoinedAt(domain.getJoinedAt());
        entity.setLeftAt(domain.getLeftAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public Member toDomain(MemberEntity entity) {
        if (entity == null) return null;
        
        return Member.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .projectId(entity.getProjectId())
                .userId(entity.getUserId())
                .role(entity.getRole())
                .allocationPercentage(entity.getAllocationPercentage())
                .status(entity.getStatus())
                .joinedAt(entity.getJoinedAt())
                .leftAt(entity.getLeftAt())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    public List<MemberEntity> toEntityList(List<Member> domains) {
        if (domains == null) return null;
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }
    
    public List<Member> toDomainList(List<MemberEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
