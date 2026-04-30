package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.dto.opportunity.req.CreateOpportunityRequest;
import com.dxc.crmservice.application.dto.opportunity.req.UpdateOpportunityRequest;
import com.dxc.crmservice.application.dto.opportunity.res.OpportunityResponse;
import com.dxc.crmservice.application.mapper.OpportunityMapper;
import com.dxc.crmservice.application.port.out.OpportunityRepository;
import com.dxc.crmservice.domain.exception.ServiceLogicException;
import com.dxc.crmservice.domain.model.aggregate.Opportunity;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Stage;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OpportunityService – Application Layer")
class OpportunityServiceTest {

    @Mock OpportunityRepository opportunityRepository;
    @Mock OpportunityMapper     opportunityMapper;

    @InjectMocks OpportunityService opportunityService;

    private static final UUID TENANT_ID  = UUID.randomUUID();
    private static final UUID CLIENT_ID  = UUID.randomUUID();
    private static final UUID OPP_ID     = UUID.randomUUID();

    @BeforeEach
    void setTenant() {
        TenantContextHolder.setTenantId(TENANT_ID.toString());
    }

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
    }

    // helpers

    private Opportunity stubOpp() {
        return Opportunity.create(TENANT_ID, CLIENT_ID, "Big deal", "desc",
                50_000.0, 60_000.0, 50, Stage.PROSPECTING, Priority.HIGH);
    }

    private OpportunityResponse stubResponse(Opportunity o) {
        return new OpportunityResponse(o.getId(), o.getClientId(), o.getTitle(),
                BigDecimal.valueOf(o.getEstimatedBudget()), o.getStage(), o.getPriority(),
                o.getEstimatedBudget(), o.getExpectedRevenue(), o.getProbability(),
                o.getExpectedStartDate(), o.getExpectedEndDate(),
                o.getLastActivityAt(), o.getNextFollowUpAt(), o.getClosingDate(),
                o.getLostReason(), o.getCreatedAt(), o.getUpdatedAt());
    }

    private CreateOpportunityRequest createRequest() {
        return new CreateOpportunityRequest(CLIENT_ID, "Big deal", "desc",
                BigDecimal.valueOf(50_000), 60_000.0, 50, Stage.PROSPECTING, null, null, Priority.HIGH);
    }

    @Nested
    @DisplayName("createOpportunity()")
    class CreateOpportunity {

        @Test
        @DisplayName("saves opportunity and returns response")
        void success() {
            Opportunity opp = stubOpp();
            OpportunityResponse response = stubResponse(opp);

            when(opportunityMapper.toDomain(any(), eq(TENANT_ID))).thenReturn(opp);
            when(opportunityMapper.toResponse(opp)).thenReturn(response);

            OpportunityResponse result = opportunityService.createOpportunity(createRequest());

            assertThat(result).isEqualTo(response);
            verify(opportunityRepository).save(opp);
        }
    }

    @Nested
    @DisplayName("getOpportunity()")
    class GetOpportunity {

        @Test
        @DisplayName("returns response for existing id")
        void success() {
            Opportunity opp = stubOpp();
            OpportunityResponse response = stubResponse(opp);

            when(opportunityRepository.findById(OPP_ID, TENANT_ID)).thenReturn(opp);
            when(opportunityMapper.toResponse(opp)).thenReturn(response);

            assertThat(opportunityService.getOpportunity(OPP_ID)).isEqualTo(response);
        }

        @Test
        @DisplayName("throws ServiceLogicException when not found")
        void notFound() {
            when(opportunityRepository.findById(OPP_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() -> opportunityService.getOpportunity(OPP_ID))
                    .isInstanceOf(ServiceLogicException.class);
        }
    }

    @Test
    @DisplayName("deleteOpportunity() delegates to repository")
    void deleteOpportunity() {
        opportunityService.deleteOpportunity(OPP_ID);

        verify(opportunityRepository).delete(OPP_ID, TENANT_ID);
    }

    @Nested
    @DisplayName("markAsWon()")
    class MarkAsWon {

        @Test
        @DisplayName("transitions to WON and persists")
        void success() {
            Opportunity opp = stubOpp();
            OpportunityResponse response = stubResponse(opp);

            when(opportunityRepository.findById(OPP_ID, TENANT_ID)).thenReturn(opp);
            when(opportunityMapper.toResponse(opp)).thenReturn(response);

            opportunityService.markAsWon(OPP_ID);

            assertThat(opp.getStage()).isEqualTo(Stage.WON);
            verify(opportunityRepository).update(opp);
        }

        @Test
        @DisplayName("throws ServiceLogicException when not found")
        void notFound() {
            when(opportunityRepository.findById(OPP_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() -> opportunityService.markAsWon(OPP_ID))
                    .isInstanceOf(ServiceLogicException.class);
        }
    }

    @Test
    @DisplayName("markAsLost() transitions to LOST and persists")
    void markAsLost() {
        Opportunity opp = stubOpp();
        OpportunityResponse response = stubResponse(opp);

        when(opportunityRepository.findById(OPP_ID, TENANT_ID)).thenReturn(opp);
        when(opportunityMapper.toResponse(opp)).thenReturn(response);

        opportunityService.markAsLost(OPP_ID, "Budget cut");

        assertThat(opp.getStage()).isEqualTo(Stage.LOST);
        assertThat(opp.getLostReason()).isEqualTo("Budget cut");
        verify(opportunityRepository).update(opp);
    }

    @Test
    @DisplayName("moveStage() transitions stage and persists")
    void moveStage() {
        Opportunity opp = stubOpp();
        OpportunityResponse response = stubResponse(opp);

        when(opportunityRepository.findById(OPP_ID, TENANT_ID)).thenReturn(opp);
        when(opportunityMapper.toResponse(opp)).thenReturn(response);

        opportunityService.moveStage(OPP_ID, Stage.QUALIFICATION);

        assertThat(opp.getStage()).isEqualTo(Stage.QUALIFICATION);
        verify(opportunityRepository).update(opp);
    }

    @Test
    @DisplayName("getAllOpportunities() maps page correctly")
    void getAllOpportunities() {
        Opportunity opp = stubOpp();
        OpportunityResponse response = stubResponse(opp);
        Page<Opportunity> page = new PageImpl<>(List.of(opp));

        when(opportunityRepository.findAll(TENANT_ID, PageRequest.of(0, 10))).thenReturn(page);
        when(opportunityMapper.toResponse(opp)).thenReturn(response);

        Page<OpportunityResponse> result = opportunityService.getAllOpportunities(PageRequest.of(0, 10));

        assertThat(result.getContent()).containsExactly(response);
    }
}
