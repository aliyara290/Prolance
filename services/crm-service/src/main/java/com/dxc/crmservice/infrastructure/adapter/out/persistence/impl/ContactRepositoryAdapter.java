package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.ContactRepository;
import com.dxc.crmservice.domain.model.entity.Contact;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.ContactEntity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.ContactRepositoryJpa;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper.ContactPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        ContactEntity contactEntity = contactPersistenceMapper.toEntity(contact);
        ContactEntity savedContactEntity = contactRepositoryJpa.save(contactEntity);
        return contactPersistenceMapper.toDomain(savedContactEntity);
    }

    @Override
    public Contact findById(UUID id, UUID tenantId) {
        return contactRepositoryJpa.findByIdAndTenantId(id, tenantId)
                .map(contactPersistenceMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Contact update(Contact contact) {
        ContactEntity contactEntity = contactPersistenceMapper.toEntity(contact);
        ContactEntity updatedContactEntity = contactRepositoryJpa.save(contactEntity);
        return contactPersistenceMapper.toDomain(updatedContactEntity);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        contactRepositoryJpa.findByIdAndTenantId(id, tenantId)
                .ifPresent(contactRepositoryJpa::delete);
    }

    @Override
    public Page<Contact> findAll(UUID tenantId, Pageable pageable) {
        Page<ContactEntity> contactEntities = contactRepositoryJpa.findByTenantId(tenantId, pageable);
        return contactEntities.map(contactPersistenceMapper::toDomain);
    }}
