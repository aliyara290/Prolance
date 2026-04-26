package com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LeadRepositoryJpa extends JpaRepository<LeadEntity, UUID> {
}
