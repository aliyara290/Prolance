package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.ContactRepository;
import com.dxc.crmservice.domain.model.entity.Contact;

import java.util.List;
import java.util.UUID;

public class ContactRepositoryAdapter implements ContactRepository {

    @Override
    public Contact save(Contact contact) {
        return null;
    }

    @Override
    public Contact findById(UUID id) {
        return null;
    }

    @Override
    public Contact update(Contact contact) {
        return null;
    }

    @Override
    public void delete(UUID id, UUID tenantId) {

    }

    @Override
    public List<Contact> findAll(UUID tenantId) {
        return List.of();
    }
}
