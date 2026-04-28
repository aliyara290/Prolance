package com.dxc.crmservice.infrastructure.adapter.in.rest.controller;

import com.dxc.crmservice.application.dto.contact.req.CreateContactRequest;
import com.dxc.crmservice.application.dto.contact.req.UpdateContactRequest;
import com.dxc.crmservice.application.dto.contact.res.ContactResponse;
import com.dxc.crmservice.application.port.in.ContactUseCase;
import com.dxc.crmservice.infrastructure.adapter.in.rest.response.ApiResponse;
import com.dxc.crmservice.infrastructure.adapter.in.rest.response.PageMeta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private final ContactUseCase contactUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<ContactResponse>> createContact(@Valid @RequestBody CreateContactRequest request) {
        ContactResponse contactResponse = contactUseCase.createContact(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(contactResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactResponse>> updateContact(@PathVariable UUID id, @Valid @RequestBody UpdateContactRequest request) {
        ContactResponse contactResponse = contactUseCase.updateContact(id, request);
        return ResponseEntity.ok(ApiResponse.success(contactResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactResponse>> getContact(@PathVariable UUID id) {
        ContactResponse contactResponse = contactUseCase.getContact(id);
        return ResponseEntity.ok(ApiResponse.success(contactResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactResponse>>> getAllContacts(Pageable pageable) {
        Page<ContactResponse> contacts = contactUseCase.getAllContacts(pageable);

        PageMeta meta = PageMeta.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .hasNext(contacts.hasNext())
                .hasPrevious(contacts.hasPrevious())
                .totalPages(contacts.getTotalPages())
                .totalElements(contacts.getTotalElements())
                .build();
        return ResponseEntity.ok(ApiResponse.success(contacts.getContent(), meta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContact(@PathVariable UUID id) {
        contactUseCase.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}
