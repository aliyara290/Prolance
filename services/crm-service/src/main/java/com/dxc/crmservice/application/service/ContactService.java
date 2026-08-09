package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.dto.contact.req.CreateContactRequest;
import com.dxc.crmservice.application.dto.contact.req.UpdateContactRequest;
import com.dxc.crmservice.application.dto.contact.res.ContactResponse;
import com.dxc.crmservice.application.mapper.ContactMapper;
import com.dxc.crmservice.application.port.in.ContactUseCase;
import com.dxc.crmservice.application.port.out.ContactRepository;
import com.dxc.crmservice.application.security.TenantGuard;
import com.dxc.crmservice.application.utils.Utils;
import com.dxc.crmservice.domain.exception.ServiceLogicException;
import com.dxc.crmservice.domain.model.entity.Contact;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import com.dxc.crmservice.domain.event.AuditLogEvent;
import com.dxc.crmservice.domain.model.valueobject.AuditAction;
import com.dxc.crmservice.domain.model.valueobject.EntityType;
import com.dxc.crmservice.infrastructure.config.TenantContextHolder;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ContactService implements ContactUseCase {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final TenantGuard tenantGuard;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ContactResponse createContact(CreateContactRequest request) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        try {
            Contact contact = contactMapper.toDomain(request, tenantId);
            Contact savedContact = contactRepository.save(contact);
            
            eventPublisher.publishEvent(new AuditLogEvent(
                    tenantId,
                    TenantContextHolder.getUserId(),
                    AuditAction.CREATE,
                    EntityType.CONTACT,
                    savedContact.getId(),
                    "Contact '" + savedContact.getFirstName() + " " + savedContact.getLastName() + "' created"
            ));
            
            return contactMapper.toResponse(savedContact);
        } catch (Exception e) {
            log.error("Error creating contact: " + e.getMessage());
            throw new ServiceLogicException("Failed to create contact");
        }
    }

    @Override
    public ContactResponse updateContact(UUID id, UpdateContactRequest request) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        try {
            Contact contact = contactRepository.findById(id, tenantId);
            if (contact == null) {
                throw new ServiceLogicException("Contact not found");
            }

            contact.updateProfile(
                    request.firstName() != null ? request.firstName() : contact.getFirstName(),
                    request.lastName() != null ? request.lastName() : contact.getLastName(),
                    request.email() != null ? request.email() : contact.getEmail(),
                    request.phone() != null ? request.phone() : contact.getPhone(),
                    request.role() != null ? request.role() : contact.getRole(),
                    request.influenceLevel() != null ? request.influenceLevel() : contact.getInfluenceLevel(),
                    request.notes() != null ? request.notes() : contact.getNotes(),
                    request.department() != null ? request.department() : contact.getDepartment(),
                    request.dateOfBirth() != null ? request.dateOfBirth() : contact.getDateOfBirth(),
                    request.secondaryEmail() != null ? request.secondaryEmail() : contact.getSecondaryEmail(),
                    request.address() != null ? contactMapper.toAddress(request.address()) : contact.getAddress(),
                    request.description() != null ? request.description() : contact.getDescription()
            );

            if (request.primary() && !contact.isPrimary()) {
                contact.markAsPrimary();
            } else if (!request.primary() && contact.isPrimary()) {
                contact.demotePrimary();
            }

            Contact updatedContact = contactRepository.update(contact);
            
            eventPublisher.publishEvent(new AuditLogEvent(
                    tenantId,
                    TenantContextHolder.getUserId(),
                    AuditAction.UPDATE,
                    EntityType.CONTACT,
                    contact.getId(),
                    "Contact '" + contact.getFirstName() + " " + contact.getLastName() + "' updated"
            ));
            
            return contactMapper.toResponse(updatedContact);
        } catch (Exception e) {
            log.error("Error updating contact: " + e.getMessage());
            throw new ServiceLogicException("Failed to update contact: " + e.getMessage());
        }
    }

    @Override
    public void deleteContact(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        try {
            contactRepository.delete(id, tenantId);
            
            eventPublisher.publishEvent(new AuditLogEvent(
                    tenantId,
                    TenantContextHolder.getUserId(),
                    AuditAction.DELETE,
                    EntityType.CONTACT,
                    id,
                    "Contact deleted"
            ));
        } catch (Exception e) {
            log.error("Error deleting contact: " + e.getMessage());
            throw new ServiceLogicException("Failed to delete contact");
        }
    }

    @Override
    public ContactResponse getContact(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        Contact contact = contactRepository.findById(id, tenantId);
        if (contact == null) {
            throw new ServiceLogicException("Contact not found");
        }
        return contactMapper.toResponse(contact);
    }

    @Override
    public Page<ContactResponse> getAllContacts(Pageable pageable) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        Page<Contact> contacts = contactRepository.findAll(tenantId, pageable);
        return contacts.map(contactMapper::toResponse);
    }
}
