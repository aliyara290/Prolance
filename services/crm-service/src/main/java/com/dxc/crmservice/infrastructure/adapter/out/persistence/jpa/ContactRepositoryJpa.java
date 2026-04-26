package com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContactRepositoryJpa extends JpaRepository<ContactEntity, UUID> {
}
