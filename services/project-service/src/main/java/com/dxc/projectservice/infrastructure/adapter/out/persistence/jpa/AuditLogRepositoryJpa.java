package com.dxc.projectservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogRepositoryJpa extends JpaRepository<AuditLogEntity, UUID> {
}
