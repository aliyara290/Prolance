package com.dxc.crmservice.application.mapper;

import com.dxc.crmservice.application.dto.contact.req.CreateContactRequest;
import com.dxc.crmservice.application.dto.contact.res.ContactResponse;
import com.dxc.crmservice.domain.model.entity.Contact;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    default Contact toDomain(CreateContactRequest request, UUID tenantId) {
        if (request == null) return null;
        return Contact.create(
                tenantId,
                request.clientId(),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone(),
                request.role(),
                request.influenceLevel(),
                request.primary(),
                request.notes()
        );
    }

    ContactResponse toResponse(Contact contact);
}
