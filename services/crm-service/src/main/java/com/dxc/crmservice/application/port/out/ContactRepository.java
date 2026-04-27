package com.dxc.crmservice.application.port.out;

import com.dxc.crmservice.domain.model.entity.Contact;

import java.util.List;
import java.util.UUID;

public interface ContactRepository {
    Contact save(Contact contact);
    Contact findById(UUID id);
    Contact update(Contact contact);
    void delete(UUID id, UUID tenantId);
    List<Contact> findAll(UUID tenantId);
}
