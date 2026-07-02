package com.dxc.crmservice.application.mapper;

import com.dxc.crmservice.application.dto.AddressDto;
import com.dxc.crmservice.application.dto.contact.req.CreateContactRequest;
import com.dxc.crmservice.application.dto.contact.res.ContactResponse;
import com.dxc.crmservice.domain.model.entity.Contact;
import com.dxc.crmservice.domain.model.valueobject.Address;
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
                request.notes(),
                request.department(),
                request.dateOfBirth(),
                request.secondaryEmail(),
                toAddress(request.address()),
                request.description(),
                null // createdBy
        );
    }

    default ContactResponse toResponse(Contact contact) {
        if (contact == null) return null;
        return new ContactResponse(
                contact.getId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getEmail(),
                contact.getPhone(),
                contact.getRole(),
                contact.getInfluenceLevel(),
                contact.isPrimary(),
                contact.getNotes(),
                contact.getClientId(),
                contact.getLastContactedAt(),
                contact.getDepartment(),
                contact.getDateOfBirth(),
                contact.getSecondaryEmail(),
                toAddressDto(contact.getAddress()),
                contact.getDescription(),
                contact.getCreatedBy(),
                contact.getUpdatedBy(),
                contact.getCreatedAt(),
                contact.getUpdatedAt()
        );
    }

    default Address toAddress(AddressDto dto) {
        if (dto == null) return null;
        return Address.builder()
                .street(dto.street())
                .city(dto.city())
                .state(dto.state())
                .zipCode(dto.zipCode())
                .country(dto.country())
                .build();
    }

    default AddressDto toAddressDto(Address address) {
        if (address == null) return null;
        return new AddressDto(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry()
        );
    }
}
