package com.dxc.crmservice.application.port.out;

import com.dxc.crmservice.domain.model.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ContactRepository {
    Contact save(Contact contact);
    Contact findById(UUID id, UUID tenantId);
    Contact update(Contact contact);
    void delete(UUID id, UUID tenantId);
    Page<Contact> findAll(UUID tenantId, Pageable pageable);
}
