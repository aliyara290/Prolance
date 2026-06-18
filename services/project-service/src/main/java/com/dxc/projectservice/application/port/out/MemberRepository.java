package com.dxc.projectservice.application.port.out;

import com.dxc.projectservice.domain.model.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MemberRepository extends GenericRepository<Member> {
    List<Member> findByProjectId(UUID projectId, UUID tenantId);
    Page<Member> findByUserId(UUID userId, UUID tenantId, Pageable pageable);
}
