package com.dxc.crmservice.infrastructure.adapter.in.rest.controller;

import com.dxc.crmservice.application.dto.opportunity.req.CreateOpportunityRequest;
import com.dxc.crmservice.application.dto.opportunity.res.OpportunityResponse;
import com.dxc.crmservice.application.port.in.OpportunityUseCase;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Stage;
import com.dxc.crmservice.infrastructure.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Adapter-in (REST controller) unit tests for OpportunityController.
 * <p>
 * Strategy:
 * - Excludes SecurityConfig (which requires JwtDecoder/Keycloak) from the slice.
 * - Imports a minimal TestSecurityConfig with permitAll HTTP + @EnableMethodSecurity.
 * - @WithMockUser injects a Security principal so @PreAuthorize role checks are
 * exercised against real granted authorities.
 */
@WebMvcTest(controllers = OpportunityController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
@Import(OpportunityControllerTest.TestSecurityConfig.class)
@DisplayName("OpportunityController – REST Adapter")
class OpportunityControllerTest {

    @org.springframework.boot.test.context.TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OpportunityUseCase opportunityUseCase;

    private static final UUID OPP_ID = UUID.randomUUID();
    private static final UUID CLIENT_ID = UUID.randomUUID();

    // helpers

    private OpportunityResponse stubResponse() {
        return new OpportunityResponse(OPP_ID, CLIENT_ID, "Big deal", BigDecimal.valueOf(50_000), Stage.PROSPECTING, Priority.HIGH, 50_000.0, 60_000.0, 50, null, null, null, null, null, null, null, null);
    }

    private CreateOpportunityRequest createRequest() {
        return new CreateOpportunityRequest(CLIENT_ID, "Big deal", "desc", BigDecimal.valueOf(50_000), 60_000.0, 50, Stage.PROSPECTING, null, null, Priority.HIGH);
    }

    @Nested
    @DisplayName("POST /api/v1/opportunities")
    class CreateOpportunity {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can create opportunity → 201")
        void adminCreates() throws Exception {
            when(opportunityUseCase.createOpportunity(any())).thenReturn(stubResponse());

            mockMvc.perform(post("/api/v1/opportunities").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createRequest()))).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(OPP_ID.toString()));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER is forbidden → 403")
        void userForbidden() throws Exception {
            mockMvc.perform(post("/api/v1/opportunities").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createRequest()))).andExpect(status().isForbidden());

        }
    }

    @Nested
    @DisplayName("GET /api/v1/opportunities/{id}")
    class GetOpportunity {

        @Test
        @WithMockUser(roles = "SALES")
        @DisplayName("SALES can read opportunity → 200")
        void salesReads() throws Exception {
            when(opportunityUseCase.getOpportunity(OPP_ID)).thenReturn(stubResponse());

            mockMvc.perform(get("/api/v1/opportunities/{id}", OPP_ID)).andExpect(status().isOk()).andExpect(jsonPath("$.data.title").value("Big deal"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/opportunities/{id}")
    class DeleteOpportunity {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can delete → 200")
        void adminDeletes() throws Exception {
            doNothing().when(opportunityUseCase).deleteOpportunity(OPP_ID);

            mockMvc.perform(delete("/api/v1/opportunities/{id}", OPP_ID)).andExpect(status().isOk());

            verify(opportunityUseCase).deleteOpportunity(OPP_ID);
        }

        @Test
        @WithMockUser(roles = "SALES")
        @DisplayName("SALES is forbidden on DELETE → 403")
        void salesForbidden() throws Exception {
            mockMvc.perform(delete("/api/v1/opportunities/{id}", OPP_ID)).andExpect(status().isForbidden());

        }
    }

    @Test
    @WithMockUser(roles = "SALES")
    @DisplayName("PATCH /{id}/won → 200 and triggers markAsWon()")
    void markAsWon() throws Exception {
        when(opportunityUseCase.markAsWon(OPP_ID)).thenReturn(stubResponse());

        mockMvc.perform(patch("/api/v1/opportunities/{id}/won", OPP_ID)).andExpect(status().isOk());

        verify(opportunityUseCase).markAsWon(OPP_ID);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /{id}/lost with reason → 200")
    void markAsLost() throws Exception {
        when(opportunityUseCase.markAsLost(eq(OPP_ID), eq("Budget cut"))).thenReturn(stubResponse());

        mockMvc.perform(patch("/api/v1/opportunities/{id}/lost", OPP_ID).param("reason", "Budget cut")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET / returns paginated list → 200")
    void getAllOpportunities() throws Exception {
        when(opportunityUseCase.getAllOpportunities(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(stubResponse())));

        mockMvc.perform(get("/api/v1/opportunities")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].title").value("Big deal"));
    }
}
