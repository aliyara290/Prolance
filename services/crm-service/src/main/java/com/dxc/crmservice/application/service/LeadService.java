package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.dto.client.req.CreateClientRequest;
import com.dxc.crmservice.application.dto.client.res.ClientResponse;
import com.dxc.crmservice.application.dto.contact.req.CreateContactRequest;
import com.dxc.crmservice.application.dto.contact.res.ContactResponse;
import com.dxc.crmservice.application.dto.lead.req.ContactRequestDTO;
import com.dxc.crmservice.application.dto.lead.req.CreateLeadRequest;
import com.dxc.crmservice.application.dto.lead.req.UpdateLeadRequest;
import com.dxc.crmservice.application.dto.lead.res.LeadResponse;
import com.dxc.crmservice.application.dto.opportunity.req.CreateOpportunityRequest;
import com.dxc.crmservice.application.dto.opportunity.res.OpportunityResponse;
import com.dxc.crmservice.application.mapper.LeadMapper;
import com.dxc.crmservice.application.port.in.ClientUseCase;
import com.dxc.crmservice.application.port.in.ContactUseCase;
import com.dxc.crmservice.application.port.in.LeadUseCase;
import com.dxc.crmservice.application.port.in.OpportunityUseCase;
import com.dxc.crmservice.application.port.out.ClientRepository;
import com.dxc.crmservice.application.port.out.ContactRepository;
import com.dxc.crmservice.application.port.out.LeadRepository;
import com.dxc.crmservice.application.port.out.feign.UserFeignPort;
import com.dxc.crmservice.application.security.TenantGuard;
import com.dxc.crmservice.application.utils.Utils;
import com.dxc.crmservice.domain.exception.RecordNotFoundException;
import com.dxc.crmservice.domain.exception.ServiceLogicException;
import com.dxc.crmservice.domain.model.aggregate.Client;
import com.dxc.crmservice.domain.model.aggregate.Lead;
import com.dxc.crmservice.domain.model.entity.Contact;
import com.dxc.crmservice.domain.model.valueobject.LeadStatus;
import com.dxc.crmservice.domain.model.valueobject.Stage;
import com.dxc.crmservice.infrastructure.adapter.out.feign.dto.ResponseWrapper;
import com.dxc.crmservice.infrastructure.adapter.out.feign.dto.UserResponseDTO;
import com.dxc.crmservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LeadService implements LeadUseCase {

    private final TenantGuard tenantGuard;
    private final LeadRepository leadRepository;
    private final ContactRepository contactRepository;
    private final ClientRepository clientRepository;
    private final LeadMapper leadMapper;
    private final ContactUseCase contactUseCase;
    private final ClientUseCase clientUseCase;
    private final OpportunityUseCase opportunityUseCase;
    private final UserFeignPort userFeignPort;


    @Override
    public OpportunityResponse  qualifyAndConvert(UUID leadId) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        Lead lead = leadRepository.findById(leadId, tenantId);
        if (lead == null) {
            throw new RecordNotFoundException("Lead not found");
        }

        if (lead.getClientId() == null) {
            throw new ServiceLogicException("Lead must have a client assigned before converting to an Opportunity");
        }

        if (lead.getStatus() != LeadStatus.QUALIFIED) {
            lead.markAsContacted();
            lead.qualify();
            leadRepository.update(lead);
        }

        CreateOpportunityRequest oppReq =
                new CreateOpportunityRequest(
                        lead.getClientId(),
                        lead.getTitle(),
                        lead.getDescription(),
                        BigDecimal.ZERO, // Default budget
                        0.0,
                        0,
                        Stage.PROSPECTING, // Default stage
                        null,
                        null,
                        lead.getPriority(),
                        null, // type
                        lead.getSource() // source
                );

        return opportunityUseCase.createOpportunity(oppReq);
    }

    @Override
    public LeadResponse createLead(CreateLeadRequest request) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        try {
            Lead lead = leadMapper.toDomain(request, tenantId);

            UUID clientId = null;
            if (request.clientId() != null) {
                log.info("Assigning client to lead");
                Client client = clientRepository.findById(request.clientId(), tenantId);
                if (client == null) {
                    throw new RecordNotFoundException("Client not found");
                }
                lead.assignClient(request.clientId());
                log.info("Client assigned to lead");
            }

            if (request.client() != null) {
                log.info("Creating client for lead");
                ClientResponse client = createClient(request.client());
                log.info("Client created for lead");
                lead.assignClient(client.id());
                clientId = client.id();
            }

            if (request.contactId() != null) {
                log.info("Adding existing contact to lead");
                Contact contact = contactRepository.findById(request.contactId(), tenantId);
                if (contact == null) {
                    throw new RecordNotFoundException("Contact not found");
                }
                lead.addContact(contact.getId());
                log.info("Contact added to lead");
            }

            if (request.contact() != null) {
                log.info("Creating contact for lead");

                ContactResponse contact = createContact(request.contact(), clientId);

                log.info("Contact created for lead");
                lead.addContact(contact.id());
            }

            if (request.assignedTo() == null) {
                throw new ServiceLogicException("Lead must have an assigned user");
            }
            ResponseWrapper<UserResponseDTO> user = userFeignPort.getUser(request.assignedTo());
            if (user.data() == null) {
                throw new RecordNotFoundException("User not found");
            }
            lead.assignTo(user.data().keycloakUserId());
            lead.createdBy(TenantContextHolder.getUserId());
            leadRepository.save(lead);
            return leadMapper.toResponse(lead);
        } catch (Exception e) {
            log.error("Error creating lead: " + e.getMessage());
            throw e;
        }
    }


    private ContactResponse createContact(ContactRequestDTO request, UUID clientId) {
        try {
            CreateContactRequest contactRequest = CreateContactRequest.builder()
                    .email(request.email())
                    .phone(request.phone())
                    .notes(request.notes())
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .role(request.role())
                    .influenceLevel(request.influenceLevel())
                    .primary(request.primary())
                    .clientId(clientId)
                    .department(request.department())
                    .dateOfBirth(request.dateOfBirth())
                    .secondaryEmail(request.secondaryEmail())
                    .address(request.address())
                    .description(request.description())
                    .build();

            return contactUseCase.createContact(contactRequest);
        } catch (Exception e) {
            log.error("Error creating contact: " + e.getMessage());
            throw e;
        }
    }

    private ClientResponse createClient(CreateClientRequest request) {
        try {
            return clientUseCase.createClient(request);
        } catch (Exception e) {
            log.error("Error creating client: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public LeadResponse updateLead(UUID id, UpdateLeadRequest request) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        try {
            Lead lead = leadRepository.findById(id, tenantId);
            if (lead == null) {
                throw new RecordNotFoundException("Lead not found");
            }

            lead.updateDetails(
                    request.title() != null ? request.title() : lead.getTitle(),
                    request.description() != null ? request.description() : lead.getDescription(),
                    request.source() != null ? request.source() : lead.getSource(),
                    request.priority() != null ? request.priority() : lead.getPriority(),
                    request.phone() != null ? request.phone() : lead.getPhone(),
                    request.industry() != null ? request.industry() : lead.getIndustry(),
                    request.annualRevenue() != null ? request.annualRevenue() : lead.getAnnualRevenue(),
                    request.company() != null ? request.company() : lead.getCompany(),
                    request.email() != null ? request.email() : lead.getEmail(),
                    request.website() != null ? request.website() : lead.getWebsite(),
                    request.numberOfEmployees() != null ? request.numberOfEmployees() : lead.getNumberOfEmployees(),
                    request.address() != null ? leadMapper.toAddress(request.address()) : lead.getAddress()
            );

            if (request.clientId() != null && !request.clientId().equals(lead.getClientId())) {
                lead.assignClient(request.clientId());
            }

            if (request.contactId() != null && !request.contactId().equals(lead.getContactId())) {
                lead.addContact(request.contactId());
            }

            Lead updatedLead = leadRepository.update(lead);
            return leadMapper.toResponse(updatedLead);
        } catch (Exception e) {
            log.error("Error updating lead: " + e.getMessage());
            throw new ServiceLogicException("Failed to update lead: " + e.getMessage());
        }
    }

    @Override
    public void deleteLead(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        try {
            Lead lead = leadRepository.findById(id, tenantId);
            if (lead == null) {
                throw new RecordNotFoundException("Lead not found");
            }
            leadRepository.delete(id, tenantId);
        } catch (Exception e) {
            log.error("Error deleting lead: " + e.getMessage());
            throw new ServiceLogicException("Failed to delete lead");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LeadResponse getLead(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        Lead lead = leadRepository.findById(id, tenantId);
        if (lead == null) {
            throw new RecordNotFoundException("Lead not found");
        }
        return leadMapper.toResponse(lead);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeadResponse> getAllLeads(Pageable pageable) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        Page<Lead> leads = leadRepository.findAll(tenantId, pageable);
        return leads.map(leadMapper::toResponse);
    }
}
