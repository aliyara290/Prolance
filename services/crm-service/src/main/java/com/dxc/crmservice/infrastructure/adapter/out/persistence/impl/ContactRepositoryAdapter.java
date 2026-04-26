package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.ContactRepository;
import com.dxc.crmservice.domain.model.entity.Contact;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.ContactEntity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.ContactRepositoryJpa;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper.ContactPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ContactRepositoryAdapter implements ContactRepository {

    private final ContactRepositoryJpa contactRepositoryJpa;
    private final ContactPersistenceMapper contactPersistenceMapper;

    @Override
    public Contact save(Contact contact) {
        ContactEntity entity = contactPersistenceMapper.toEntity(contact);
        return contactPersistenceMapper.toDomain(contactRepositoryJpa.save(entity));
    }

    @Override
    public Contact findById(UUID id) {
        return contactRepositoryJpa.findById(id)
                .map(contactPersistenceMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Contact update(Contact contact) {
        return save(contact);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        contactRepositoryJpa.findByIdAndTenantId(id, tenantId).ifPresent(contactRepositoryJpa::delete);
    }

    @Override
    public List<Contact> findAll(UUID tenantId) {
        return contactRepositoryJpa.findByTenantId(tenantId).stream()
                .map(contactPersistenceMapper::toDomain)
                .toList();
    }
}
