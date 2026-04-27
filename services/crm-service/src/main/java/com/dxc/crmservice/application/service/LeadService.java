package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.dto.lead.req.CreateLeadRequest;
import com.dxc.crmservice.application.dto.lead.req.UpdateLeadRequest;
import com.dxc.crmservice.application.dto.lead.res.LeadResponse;
import com.dxc.crmservice.application.mapper.LeadMapper;
import com.dxc.crmservice.application.port.in.LeadUseCase;
import com.dxc.crmservice.application.port.out.LeadRepository;
import com.dxc.crmservice.application.utils.Utils;
import com.dxc.crmservice.domain.exception.ServiceLogicException;
import com.dxc.crmservice.domain.model.aggregate.Lead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LeadService implements LeadUseCase {

    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;

    @Override
    public LeadResponse createLead(CreateLeadRequest request) {
        UUID tenantId = Utils.resolveTenantId();
        try{
            Lead lead = leadMapper.toDomain(request, tenantId);
            if (request.contactId() != null) {
                lead.addContact(request.contactId());
            }

            if (request.clientId() != null) {
                lead.assignClient(request.clientId());
            }

            lead.assignTo(request.assignedTo());

            leadRepository.save(lead);
            return leadMapper.toResponse(lead);
        } catch (Exception e) {
            log.error("Error creating lead: " + e.getMessage());
            throw new ServiceLogicException("Failed to create lead");
        }
    }

    @Override
    public LeadResponse updateLead(UUID id, UpdateLeadRequest request) {
        UUID tenantId = Utils.resolveTenantId();
        try {
            Lead lead = leadRepository.findById(id, tenantId);
            if (lead == null) {
                throw new ServiceLogicException("Lead not found");
            }
            
            lead.updateDetails(
                    request.title() != null ? request.title() : lead.getTitle(),
                    request.description() != null ? request.description() : lead.getDescription(),
                    request.source() != null ? request.source() : lead.getSource(),
                    request.priority() != null ? request.priority() : lead.getPriority()
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
        try {
            leadRepository.delete(id, tenantId);
        } catch (Exception e) {
            log.error("Error deleting lead: " + e.getMessage());
            throw new ServiceLogicException("Failed to delete lead");
        }
    }

    @Override
    public LeadResponse getLead(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        Lead lead = leadRepository.findById(id, tenantId);
        if (lead == null) {
            throw new ServiceLogicException("Lead not found");
        }
        return leadMapper.toResponse(lead);
    }

    @Override
    public Page<LeadResponse> getAllLeads(Pageable pageable) {
        UUID tenantId = Utils.resolveTenantId();
        Page<Lead> leads = leadRepository.findAll(tenantId, pageable);
        return leads.map(leadMapper::toResponse);
    }
}
