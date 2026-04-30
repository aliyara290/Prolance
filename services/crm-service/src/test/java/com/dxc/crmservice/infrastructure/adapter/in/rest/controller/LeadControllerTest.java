package com.dxc.crmservice.infrastructure.adapter.in.rest.controller;

import com.dxc.crmservice.application.dto.lead.req.CreateLeadRequest;
import com.dxc.crmservice.application.dto.lead.req.UpdateLeadRequest;
import com.dxc.crmservice.application.dto.lead.res.LeadResponse;
import com.dxc.crmservice.application.dto.opportunity.res.OpportunityResponse;
import com.dxc.crmservice.application.port.in.LeadUseCase;
import com.dxc.crmservice.domain.model.valueobject.LeadStatus;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Source;
import com.dxc.crmservice.domain.model.valueobject.Stage;
import com.dxc.crmservice.infrastructure.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = LeadController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
@Import(LeadControllerTest.TestSecurityConfig.class)
@DisplayName("LeadController – REST Adapter")
class LeadControllerTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean LeadUseCase leadUseCase;

    private static final UUID LEAD_ID   = UUID.randomUUID();
    private static final UUID CLIENT_ID = UUID.randomUUID();
    private static final UUID USER_ID   = UUID.randomUUID();

    // helpers

    private LeadResponse stubResponse() {
        return new LeadResponse(LEAD_ID, "Acme deal", "description",
                Source.WEBSITE, Priority.MEDIUM, LeadStatus.NEW,
                CLIENT_ID, null, USER_ID, null, null,
                LocalDateTime.now(), LocalDateTime.now());
    }

    private CreateLeadRequest createRequest() {
        return new CreateLeadRequest("Acme deal", "description",
                Source.WEBSITE, Priority.MEDIUM, CLIENT_ID, CONTACT_ID, null, null, USER_ID);
    }

    private static final UUID CONTACT_ID = UUID.randomUUID();

    private OpportunityResponse stubOppResponse() {
        return new OpportunityResponse(UUID.randomUUID(), CLIENT_ID, "Acme deal",
                BigDecimal.ZERO, Stage.PROSPECTING, Priority.MEDIUM,
                0.0, 0.0, 0, null, null, null, null, null, null, null, null);
    }

    // ── POST /api/v1/leads ────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/v1/leads")
    class CreateLead {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can create lead → 201")
        void adminCreates() throws Exception {
            when(leadUseCase.createLead(any())).thenReturn(stubResponse());

            mockMvc.perform(post("/api/v1/leads")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.title").value("Acme deal"));
        }

        @Test
        @WithMockUser(roles = "SALES")
        @DisplayName("SALES can create lead → 201")
        void salesCreates() throws Exception {
            when(leadUseCase.createLead(any())).thenReturn(stubResponse());

            mockMvc.perform(post("/api/v1/leads")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest())))
                    .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER is forbidden → 403")
        void userForbidden() throws Exception {
            mockMvc.perform(post("/api/v1/leads")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest())))
                    .andExpect(status().isForbidden());
        }
    }

    // ── PUT /api/v1/leads/{id} ────────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /api/v1/leads/{id}")
    class UpdateLead {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can update → 200")
        void adminUpdates() throws Exception {
            when(leadUseCase.updateLead(eq(LEAD_ID), any())).thenReturn(stubResponse());

            UpdateLeadRequest req = new UpdateLeadRequest("New title", null, null, null, null, null);
            mockMvc.perform(put("/api/v1/leads/{id}", LEAD_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(LEAD_ID.toString()));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER is forbidden → 403")
        void userForbidden() throws Exception {
            UpdateLeadRequest req = new UpdateLeadRequest("New title", null, null, null, null, null);
            mockMvc.perform(put("/api/v1/leads/{id}", LEAD_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isForbidden());
        }
    }

    // ── GET /api/v1/leads/{id} ────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/leads/{id}")
    class GetLead {

        @Test
        @WithMockUser(roles = "SALES")
        @DisplayName("SALES can read → 200")
        void salesReads() throws Exception {
            when(leadUseCase.getLead(LEAD_ID)).thenReturn(stubResponse());

            mockMvc.perform(get("/api/v1/leads/{id}", LEAD_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.title").value("Acme deal"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER can read → 200")
        void userReads() throws Exception {
            when(leadUseCase.getLead(LEAD_ID)).thenReturn(stubResponse());

            mockMvc.perform(get("/api/v1/leads/{id}", LEAD_ID))
                    .andExpect(status().isOk());
        }
    }

    // ── GET /api/v1/leads ─────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET / returns paginated list → 200")
    void getAllLeads() throws Exception {
        when(leadUseCase.getAllLeads(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(stubResponse())));

        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Acme deal"));
    }

    // ── DELETE /api/v1/leads/{id} ─────────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/v1/leads/{id}")
    class DeleteLead {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can delete → 200")
        void adminDeletes() throws Exception {
            doNothing().when(leadUseCase).deleteLead(LEAD_ID);

            mockMvc.perform(delete("/api/v1/leads/{id}", LEAD_ID))
                    .andExpect(status().isOk());

            verify(leadUseCase).deleteLead(LEAD_ID);
        }

        @Test
        @WithMockUser(roles = "SALES")
        @DisplayName("SALES is forbidden on DELETE → 403")
        void salesForbidden() throws Exception {
            mockMvc.perform(delete("/api/v1/leads/{id}", LEAD_ID))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER is forbidden on DELETE → 403")
        void userForbidden() throws Exception {
            mockMvc.perform(delete("/api/v1/leads/{id}", LEAD_ID))
                    .andExpect(status().isForbidden());
        }
    }

    // ── POST /api/v1/leads/{id}/convert ───────────────────────────────────────

    @Nested
    @DisplayName("POST /api/v1/leads/{id}/convert")
    class ConvertLead {

        @Test
        @WithMockUser(roles = "SALES")
        @DisplayName("SALES can convert lead → 200")
        void salesConverts() throws Exception {
            when(leadUseCase.qualifyAndConvert(LEAD_ID)).thenReturn(stubOppResponse());

            mockMvc.perform(post("/api/v1/leads/{id}/convert", LEAD_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.title").value("Acme deal"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER is forbidden on convert → 403")
        void userForbidden() throws Exception {
            mockMvc.perform(post("/api/v1/leads/{id}/convert", LEAD_ID))
                    .andExpect(status().isForbidden());
        }
    }
}
