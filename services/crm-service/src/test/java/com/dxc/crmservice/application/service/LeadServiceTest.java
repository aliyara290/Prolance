package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.dto.lead.req.CreateLeadRequest;
import com.dxc.crmservice.application.dto.lead.req.UpdateLeadRequest;
import com.dxc.crmservice.application.dto.lead.res.LeadResponse;
import com.dxc.crmservice.application.dto.opportunity.res.OpportunityResponse;
import com.dxc.crmservice.application.mapper.LeadMapper;
import com.dxc.crmservice.application.port.in.ClientUseCase;
import com.dxc.crmservice.application.port.in.ContactUseCase;
import com.dxc.crmservice.application.port.in.OpportunityUseCase;
import com.dxc.crmservice.application.port.out.ClientRepository;
import com.dxc.crmservice.application.port.out.ContactRepository;
import com.dxc.crmservice.application.port.out.LeadRepository;
import com.dxc.crmservice.application.port.out.feign.UserFeignPort;
import com.dxc.crmservice.domain.exception.RecordNotFoundException;
import com.dxc.crmservice.domain.exception.ServiceLogicException;
import com.dxc.crmservice.domain.model.aggregate.Client;
import com.dxc.crmservice.domain.model.aggregate.Lead;
import com.dxc.crmservice.domain.model.entity.Contact;
import com.dxc.crmservice.domain.model.valueobject.*;
import com.dxc.crmservice.infrastructure.adapter.out.feign.dto.ResponseWrapper;
import com.dxc.crmservice.infrastructure.adapter.out.feign.dto.UserResponseDTO;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LeadService – Application Layer")
class LeadServiceTest {

    @Mock LeadRepository      leadRepository;
    @Mock ContactRepository   contactRepository;
    @Mock ClientRepository    clientRepository;
    @Mock LeadMapper          leadMapper;
    @Mock ContactUseCase      contactUseCase;
    @Mock ClientUseCase       clientUseCase;
    @Mock OpportunityUseCase  opportunityUseCase;
    @Mock
    UserFeignPort userFeignPort;

    @InjectMocks LeadService leadService;

    private static final UUID TENANT_ID  = UUID.randomUUID();
    private static final UUID LEAD_ID    = UUID.randomUUID();
    private static final UUID CLIENT_ID  = UUID.randomUUID();
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final UUID USER_ID    = UUID.randomUUID();

    @BeforeEach
    void setTenant() {
        TenantContextHolder.setTenantId(TENANT_ID.toString());
    }

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
    }

    // helpers

    private Lead stubLead() {
        return Lead.create(TENANT_ID, "Acme deal", "description", Source.WEBSITE, Priority.MEDIUM);
    }

    private LeadResponse stubResponse(Lead lead) {
        return new LeadResponse(lead.getId(), lead.getTitle(), lead.getDescription(),
                lead.getSource(), lead.getPriority(), lead.getStatus(),
                lead.getClientId(), lead.getContactId(), lead.getAssignedTo(),
                null, null, lead.getCreatedAt(), lead.getUpdatedAt());
    }

    private Client stubClient() {
        return Client.create(TENANT_ID, "Acme Corp", "Tech", null, null, null, ClientType.B2B, Source.WEBSITE);
    }

    private Contact stubContact() {
        return Contact.create(TENANT_ID, CLIENT_ID, "John", "Doe",
                "john@acme.com", null, Role.CEO, InfluenceLevel.HIGH, false, null);
    }

    private ResponseWrapper<UserResponseDTO> stubUserResponse() {
        UserResponseDTO user = UserResponseDTO.builder()
                .id(USER_ID)
                .tenantId(TENANT_ID)
                .email("john@acme.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        return new ResponseWrapper<>(user);
    }

    @Nested
    @DisplayName("createLead()")
    class CreateLead {

        @Test
        @DisplayName("creates lead with existing clientId and assignedTo")
        void withExistingClient() {
            Lead lead = stubLead();
            LeadResponse response = stubResponse(lead);
            Client client = stubClient();

            CreateLeadRequest request = new CreateLeadRequest("Acme deal", "desc",
                    Source.WEBSITE, Priority.MEDIUM, CLIENT_ID, null, null, null, USER_ID);

            when(leadMapper.toDomain(any(), eq(TENANT_ID))).thenReturn(lead);
            when(clientRepository.findById(CLIENT_ID, TENANT_ID)).thenReturn(client);
            when(userFeignPort.getUser(USER_ID)).thenReturn(stubUserResponse());
            when(leadMapper.toResponse(lead)).thenReturn(response);

            LeadResponse result = leadService.createLead(request);

            assertThat(result).isEqualTo(response);
            verify(leadRepository).save(lead);
        }

        @Test
        @DisplayName("throws RecordNotFoundException when clientId not found")
        void clientNotFound() {
            Lead lead = stubLead();
            CreateLeadRequest request = new CreateLeadRequest("Acme deal", "desc",
                    Source.WEBSITE, Priority.MEDIUM, CLIENT_ID, null, null, null, USER_ID);

            when(leadMapper.toDomain(any(), eq(TENANT_ID))).thenReturn(lead);
            when(clientRepository.findById(CLIENT_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() -> leadService.createLead(request))
                    .isInstanceOf(RecordNotFoundException.class);
        }

        @Test
        @DisplayName("creates lead with existing contactId")
        void withExistingContact() {
            Lead lead = stubLead();
            LeadResponse response = stubResponse(lead);
            Contact contact = stubContact();

            CreateLeadRequest request = new CreateLeadRequest("Acme deal", "desc",
                    Source.WEBSITE, Priority.MEDIUM, null, CONTACT_ID, null, null, USER_ID);

            when(leadMapper.toDomain(any(), eq(TENANT_ID))).thenReturn(lead);
            when(contactRepository.findById(CONTACT_ID, TENANT_ID)).thenReturn(contact);
            when(userFeignPort.getUser(USER_ID)).thenReturn(stubUserResponse());
            when(leadMapper.toResponse(lead)).thenReturn(response);

            LeadResponse result = leadService.createLead(request);

            assertThat(result).isEqualTo(response);
            verify(leadRepository).save(lead);
        }

        @Test
        @DisplayName("throws RecordNotFoundException when contactId not found")
        void contactNotFound() {
            Lead lead = stubLead();
            CreateLeadRequest request = new CreateLeadRequest("Acme deal", "desc",
                    Source.WEBSITE, Priority.MEDIUM, null, CONTACT_ID, null, null, USER_ID);

            when(leadMapper.toDomain(any(), eq(TENANT_ID))).thenReturn(lead);
            when(contactRepository.findById(CONTACT_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() -> leadService.createLead(request))
                    .isInstanceOf(RecordNotFoundException.class);
        }

        @Test
        @DisplayName("throws ServiceLogicException when assignedTo is null")
        void noAssignedTo() {
            Lead lead = stubLead();
            CreateLeadRequest request = new CreateLeadRequest("Acme deal", "desc",
                    Source.WEBSITE, Priority.MEDIUM, null, null, null, null, null);

            when(leadMapper.toDomain(any(), eq(TENANT_ID))).thenReturn(lead);

            assertThatThrownBy(() -> leadService.createLead(request))
                    .isInstanceOf(ServiceLogicException.class)
                    .hasMessageContaining("assigned user");
        }
    }

    @Nested
    @DisplayName("updateLead()")
    class UpdateLead {

        @Test
        @DisplayName("updates lead details and returns response")
        void success() {
            Lead lead = stubLead();
            LeadResponse response = stubResponse(lead);

            when(leadRepository.findById(LEAD_ID, TENANT_ID)).thenReturn(lead);
            when(leadRepository.update(lead)).thenReturn(lead);
            when(leadMapper.toResponse(lead)).thenReturn(response);

            UpdateLeadRequest req = new UpdateLeadRequest("New title", "new desc", Source.REFERRAL, Priority.HIGH, null, null);
            LeadResponse result = leadService.updateLead(LEAD_ID, req);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws ServiceLogicException when lead not found")
        void notFound() {
            when(leadRepository.findById(LEAD_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() ->
                    leadService.updateLead(LEAD_ID, new UpdateLeadRequest("t", null, null, null, null, null)))
                    .isInstanceOf(ServiceLogicException.class);
        }
    }

    @Nested
    @DisplayName("deleteLead()")
    class DeleteLead {

        @Test
        @DisplayName("deletes existing lead")
        void success() {
            Lead lead = stubLead();
            when(leadRepository.findById(LEAD_ID, TENANT_ID)).thenReturn(lead);

            leadService.deleteLead(LEAD_ID);

            verify(leadRepository).delete(LEAD_ID, TENANT_ID);
        }

        @Test
        @DisplayName("throws ServiceLogicException when lead not found")
        void notFound() {
            when(leadRepository.findById(LEAD_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() -> leadService.deleteLead(LEAD_ID))
                    .isInstanceOf(ServiceLogicException.class);
        }
    }

    @Nested
    @DisplayName("getLead()")
    class GetLead {

        @Test
        @DisplayName("returns lead response for existing id")
        void success() {
            Lead lead = stubLead();
            LeadResponse response = stubResponse(lead);

            when(leadRepository.findById(LEAD_ID, TENANT_ID)).thenReturn(lead);
            when(leadMapper.toResponse(lead)).thenReturn(response);

            LeadResponse result = leadService.getLead(LEAD_ID);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws RecordNotFoundException when not found")
        void notFound() {
            when(leadRepository.findById(LEAD_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() -> leadService.getLead(LEAD_ID))
                    .isInstanceOf(RecordNotFoundException.class);
        }
    }

    @Test
    @DisplayName("getAllLeads() maps page correctly")
    void getAllLeads() {
        Lead lead = stubLead();
        LeadResponse response = stubResponse(lead);
        Page<Lead> page = new PageImpl<>(List.of(lead));

        when(leadRepository.findAll(TENANT_ID, PageRequest.of(0, 10))).thenReturn(page);
        when(leadMapper.toResponse(lead)).thenReturn(response);

        Page<LeadResponse> result = leadService.getAllLeads(PageRequest.of(0, 10));

        assertThat(result.getContent()).containsExactly(response);
    }

    @Nested
    @DisplayName("qualifyAndConvert()")
    class QualifyAndConvert {

        @Test
        @DisplayName("qualifies NEW lead and converts to opportunity")
        void successFromNew() {
            Lead lead = stubLead();
            lead.assignClient(CLIENT_ID);

            OpportunityResponse oppResponse = new OpportunityResponse(UUID.randomUUID(), CLIENT_ID,
                    "Acme deal", BigDecimal.ZERO, Stage.PROSPECTING, Priority.MEDIUM,
                    0.0, 0.0, 0, null, null, null, null, null, null, null, null);

            when(leadRepository.findById(LEAD_ID, TENANT_ID)).thenReturn(lead);
            when(opportunityUseCase.createOpportunity(any())).thenReturn(oppResponse);

            OpportunityResponse result = leadService.qualifyAndConvert(LEAD_ID);

            assertThat(result).isEqualTo(oppResponse);
            assertThat(lead.getStatus()).isEqualTo(LeadStatus.QUALIFIED);
            verify(leadRepository).update(lead);
        }

        @Test
        @DisplayName("throws RecordNotFoundException when lead not found")
        void notFound() {
            when(leadRepository.findById(LEAD_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() -> leadService.qualifyAndConvert(LEAD_ID))
                    .isInstanceOf(RecordNotFoundException.class);
        }

        @Test
        @DisplayName("throws ServiceLogicException when lead has no client")
        void noClient() {
            Lead lead = stubLead();
            when(leadRepository.findById(LEAD_ID, TENANT_ID)).thenReturn(lead);

            assertThatThrownBy(() -> leadService.qualifyAndConvert(LEAD_ID))
                    .isInstanceOf(ServiceLogicException.class)
                    .hasMessageContaining("client assigned");
        }

        @Test
        @DisplayName("skips qualification for already-qualified lead")
        void alreadyQualified() {
            Lead lead = Lead.rehydrate(LEAD_ID, TENANT_ID, CLIENT_ID, null,
                    "Acme deal", "desc", Source.WEBSITE, LeadStatus.QUALIFIED,
                    50, Priority.MEDIUM, USER_ID, LocalDateTime.now(), LocalDateTime.now(),
                    null, LocalDateTime.now(), LocalDateTime.now());

            OpportunityResponse oppResponse = new OpportunityResponse(UUID.randomUUID(), CLIENT_ID,
                    "Acme deal", BigDecimal.ZERO, Stage.PROSPECTING, Priority.MEDIUM,
                    0.0, 0.0, 0, null, null, null, null, null, null, null, null);

            when(leadRepository.findById(LEAD_ID, TENANT_ID)).thenReturn(lead);
            when(opportunityUseCase.createOpportunity(any())).thenReturn(oppResponse);

            OpportunityResponse result = leadService.qualifyAndConvert(LEAD_ID);

            assertThat(result).isEqualTo(oppResponse);
            verify(leadRepository, never()).update(any());
        }
    }
}
