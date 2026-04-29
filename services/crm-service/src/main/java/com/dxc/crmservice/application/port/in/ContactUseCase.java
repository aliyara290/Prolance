package com.dxc.crmservice.application.port.in;

import com.dxc.crmservice.application.dto.contact.req.CreateContactRequest;
import com.dxc.crmservice.application.dto.contact.req.UpdateContactRequest;
import com.dxc.crmservice.application.dto.contact.res.ContactResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ContactUseCase {
    ContactResponse createContact(CreateContactRequest request);
    ContactResponse updateContact(UUID id, UpdateContactRequest request);
    void deleteContact(UUID id);
    ContactResponse getContact(UUID id);
    Page<ContactResponse> getAllContacts(Pageable pageable);
}
