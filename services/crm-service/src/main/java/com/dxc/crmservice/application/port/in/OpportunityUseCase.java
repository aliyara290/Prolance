package com.dxc.crmservice.application.port.in;

import com.dxc.crmservice.application.dto.opportunity.req.CreateOpportunityRequest;
import com.dxc.crmservice.application.dto.opportunity.req.UpdateOpportunityRequest;
import com.dxc.crmservice.application.dto.opportunity.res.OpportunityResponse;
import com.dxc.crmservice.domain.model.valueobject.Stage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OpportunityUseCase {

    OpportunityResponse createOpportunity(CreateOpportunityRequest request);
    OpportunityResponse updateOpportunity(UUID id, UpdateOpportunityRequest request);
    void deleteOpportunity(UUID id);
    OpportunityResponse getOpportunity(UUID id);
    Page<OpportunityResponse> getAllOpportunities(Pageable pageable);
    
    OpportunityResponse moveStage(UUID id, Stage stage);
    OpportunityResponse markAsWon(UUID id);
    OpportunityResponse markAsLost(UUID id, String reason);
}