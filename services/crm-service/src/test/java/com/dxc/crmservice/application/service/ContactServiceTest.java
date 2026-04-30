package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.dto.contact.req.CreateContactRequest;
import com.dxc.crmservice.application.dto.contact.req.UpdateContactRequest;
import com.dxc.crmservice.application.dto.contact.res.ContactResponse;
import com.dxc.crmservice.application.mapper.ContactMapper;
import com.dxc.crmservice.application.port.out.ContactRepository;
import com.dxc.crmservice.domain.exception.ServiceLogicException;
import com.dxc.crmservice.domain.model.entity.Contact;
import com.dxc.crmservice.domain.model.valueobject.InfluenceLevel;
import com.dxc.crmservice.domain.model.valueobject.Role;
import com.dxc.crmservice.infrastructure.config.TenantContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContactService – Application Layer")
class ContactServiceTest {

    @Mock ContactRepository contactRepository;
    @Mock ContactMapper     contactMapper;

    @InjectMocks ContactService contactService;

    private static final UUID TENANT_ID  = UUID.randomUUID();
    private static final UUID CLIENT_ID  = UUID.randomUUID();
    private static final UUID CONTACT_ID = UUID.randomUUID();

    @BeforeEach
    void setTenant() {
        TenantContextHolder.setTenantId(TENANT_ID.toString());
    }

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
    }

    // helpers

    private Contact stubContact() {
        return Contact.create(TENANT_ID, CLIENT_ID, "John", "Doe",
                "john@acme.com", "+33600000000", Role.CEO, InfluenceLevel.HIGH,
                true, "Key contact");
    }

    private ContactResponse stubResponse(Contact contact) {
        return new ContactResponse(contact.getId(), contact.getFirstName(), contact.getLastName(),
                contact.getEmail(), contact.getPhone(), contact.getRole(),
                contact.getInfluenceLevel(), contact.isPrimary(), contact.getNotes(),
                contact.getClientId(), contact.getLastContactedAt(),
                contact.getCreatedAt(), contact.getUpdatedAt());
    }

    private CreateContactRequest createRequest() {
        return CreateContactRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@acme.com")
                .phone("+33600000000")
                .role(Role.CEO)
                .influenceLevel(InfluenceLevel.HIGH)
                .primary(true)
                .notes("Key contact")
                .clientId(CLIENT_ID)
                .build();
    }

    @Nested
    @DisplayName("createContact()")
    class CreateContact {

        @Test
        @DisplayName("saves contact and returns response")
        void success() {
            Contact contact = stubContact();
            ContactResponse response = stubResponse(contact);

            when(contactMapper.toDomain(any(), eq(TENANT_ID))).thenReturn(contact);
            when(contactRepository.save(contact)).thenReturn(contact);
            when(contactMapper.toResponse(contact)).thenReturn(response);

            ContactResponse result = contactService.createContact(createRequest());

            assertThat(result).isEqualTo(response);
            verify(contactRepository).save(contact);
        }

        @Test
        @DisplayName("wraps repository exception in ServiceLogicException")
        void repositoryFailure() {
            when(contactMapper.toDomain(any(), eq(TENANT_ID))).thenThrow(new RuntimeException("DB error"));

            assertThatThrownBy(() -> contactService.createContact(createRequest()))
                    .isInstanceOf(ServiceLogicException.class);
        }
    }

    @Nested
    @DisplayName("updateContact()")
    class UpdateContact {

        @Test
        @DisplayName("updates profile and returns response")
        void success() {
            Contact contact = stubContact();
            ContactResponse response = stubResponse(contact);

            when(contactRepository.findById(CONTACT_ID, TENANT_ID)).thenReturn(contact);
            when(contactRepository.update(contact)).thenReturn(contact);
            when(contactMapper.toResponse(contact)).thenReturn(response);

            UpdateContactRequest req = new UpdateContactRequest(
                    "Jane", "Doe", "jane@acme.com", null, Role.CEO, InfluenceLevel.HIGH, true, null, null);
            ContactResponse result = contactService.updateContact(CONTACT_ID, req);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws ServiceLogicException when contact not found")
        void notFound() {
            when(contactRepository.findById(CONTACT_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() ->
                    contactService.updateContact(CONTACT_ID,
                            new UpdateContactRequest("Jane", null, null, null, null, null, false, null, null)))
                    .isInstanceOf(ServiceLogicException.class);
        }
    }

    @Nested
    @DisplayName("getContact()")
    class GetContact {

        @Test
        @DisplayName("returns contact response for existing id")
        void success() {
            Contact contact = stubContact();
            ContactResponse response = stubResponse(contact);

            when(contactRepository.findById(CONTACT_ID, TENANT_ID)).thenReturn(contact);
            when(contactMapper.toResponse(contact)).thenReturn(response);

            ContactResponse result = contactService.getContact(CONTACT_ID);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws ServiceLogicException when not found")
        void notFound() {
            when(contactRepository.findById(CONTACT_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() -> contactService.getContact(CONTACT_ID))
                    .isInstanceOf(ServiceLogicException.class);
        }
    }

    @Test
    @DisplayName("deleteContact() delegates to repository")
    void deleteContact() {
        contactService.deleteContact(CONTACT_ID);

        verify(contactRepository).delete(CONTACT_ID, TENANT_ID);
    }

    @Test
    @DisplayName("deleteContact() wraps exception in ServiceLogicException")
    void deleteContactFailure() {
        doThrow(new RuntimeException("DB error")).when(contactRepository).delete(CONTACT_ID, TENANT_ID);

        assertThatThrownBy(() -> contactService.deleteContact(CONTACT_ID))
                .isInstanceOf(ServiceLogicException.class);
    }

    @Test
    @DisplayName("getAllContacts() maps page correctly")
    void getAllContacts() {
        Contact contact = stubContact();
        ContactResponse response = stubResponse(contact);
        Page<Contact> page = new PageImpl<>(List.of(contact));

        when(contactRepository.findAll(TENANT_ID, PageRequest.of(0, 10))).thenReturn(page);
        when(contactMapper.toResponse(contact)).thenReturn(response);

        Page<ContactResponse> result = contactService.getAllContacts(PageRequest.of(0, 10));

        assertThat(result.getContent()).containsExactly(response);
    }
}
