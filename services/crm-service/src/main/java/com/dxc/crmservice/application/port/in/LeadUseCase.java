package com.dxc.crmservice.application.port.in;

import com.dxc.crmservice.application.dto.lead.req.CreateLeadRequest;
import com.dxc.crmservice.application.dto.lead.req.UpdateLeadRequest;
import com.dxc.crmservice.application.dto.lead.res.LeadResponse;
import com.dxc.crmservice.application.dto.opportunity.res.OpportunityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LeadUseCase {
     LeadResponse createLead(CreateLeadRequest request);
     LeadResponse updateLead(UUID id, UpdateLeadRequest request);
     void deleteLead(UUID id);
     LeadResponse getLead(UUID id);
     Page<LeadResponse> getAllLeads(Pageable pageable);
     OpportunityResponse qualifyAndConvert(UUID leadId);
}