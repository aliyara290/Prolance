package com.dxc.crmservice.infrastructure.adapter.in.rest.controller;

import com.dxc.crmservice.application.dto.client.req.CreateClientRequest;
import com.dxc.crmservice.application.dto.client.req.UpdateClientRequest;
import com.dxc.crmservice.application.dto.client.res.ClientResponse;
import com.dxc.crmservice.application.port.in.ClientUseCase;
import com.dxc.crmservice.domain.model.valueobject.ClientStatus;
import com.dxc.crmservice.domain.model.valueobject.ClientType;
import com.dxc.crmservice.domain.model.valueobject.Source;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ClientController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
@Import(ClientControllerTest.TestSecurityConfig.class)
@DisplayName("ClientController – REST Adapter")
class ClientControllerTest {

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

    @MockBean ClientUseCase clientUseCase;

    private static final UUID CLIENT_ID = UUID.randomUUID();

    // helpers

    private ClientResponse stubResponse() {
        return new ClientResponse(CLIENT_ID, "Acme Corp", "Tech",
                "https://acme.com", "+33600000000", null, null,
                ClientStatus.ACTIVE, ClientType.B2B, Source.WEBSITE,
                LocalDateTime.now(), LocalDateTime.now());
    }

    private CreateClientRequest createRequest() {
        return new CreateClientRequest("Acme Corp", "Tech",
                "https://acme.com", "+33600000000", null, ClientType.B2B, Source.WEBSITE);
    }

    // ── POST /api/v1/clients ──────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/v1/clients")
    class CreateClient {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can create client → 201")
        void adminCreates() throws Exception {
            when(clientUseCase.createClient(any())).thenReturn(stubResponse());

            mockMvc.perform(post("/api/v1/clients")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.name").value("Acme Corp"));
        }

        @Test
        @WithMockUser(roles = "SALES")
        @DisplayName("SALES can create client → 201")
        void salesCreates() throws Exception {
            when(clientUseCase.createClient(any())).thenReturn(stubResponse());

            mockMvc.perform(post("/api/v1/clients")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest())))
                    .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER is forbidden → 403")
        void userForbidden() throws Exception {
            mockMvc.perform(post("/api/v1/clients")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest())))
                    .andExpect(status().isForbidden());
        }
    }

    // ── PUT /api/v1/clients/{id} ──────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /api/v1/clients/{id}")
    class UpdateClient {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can update → 200")
        void adminUpdates() throws Exception {
            when(clientUseCase.updateClient(eq(CLIENT_ID), any())).thenReturn(stubResponse());

            UpdateClientRequest req = new UpdateClientRequest("New Name", null, null, null, null, null, null, null);
            mockMvc.perform(put("/api/v1/clients/{id}", CLIENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(CLIENT_ID.toString()));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER is forbidden → 403")
        void userForbidden() throws Exception {
            UpdateClientRequest req = new UpdateClientRequest("New Name", null, null, null, null, null, null, null);
            mockMvc.perform(put("/api/v1/clients/{id}", CLIENT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isForbidden());
        }
    }

    // ── GET /api/v1/clients/{id} ──────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/clients/{id}")
    class GetClient {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can read → 200")
        void adminReads() throws Exception {
            when(clientUseCase.getClient(CLIENT_ID)).thenReturn(stubResponse());

            mockMvc.perform(get("/api/v1/clients/{id}", CLIENT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.name").value("Acme Corp"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER can read → 200")
        void userReads() throws Exception {
            when(clientUseCase.getClient(CLIENT_ID)).thenReturn(stubResponse());

            mockMvc.perform(get("/api/v1/clients/{id}", CLIENT_ID))
                    .andExpect(status().isOk());
        }
    }

    // ── GET /api/v1/clients ───────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET / returns paginated list → 200")
    void getAllClients() throws Exception {
        when(clientUseCase.getAllClients(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(stubResponse())));

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Acme Corp"));
    }

    // ── DELETE /api/v1/clients/{id} ───────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/v1/clients/{id}")
    class DeleteClient {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can delete → 200")
        void adminDeletes() throws Exception {
            doNothing().when(clientUseCase).deleteClient(CLIENT_ID);

            mockMvc.perform(delete("/api/v1/clients/{id}", CLIENT_ID))
                    .andExpect(status().isOk());

            verify(clientUseCase).deleteClient(CLIENT_ID);
        }

        @Test
        @WithMockUser(roles = "SALES")
        @DisplayName("SALES is forbidden on DELETE → 403")
        void salesForbidden() throws Exception {
            mockMvc.perform(delete("/api/v1/clients/{id}", CLIENT_ID))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER is forbidden on DELETE → 403")
        void userForbidden() throws Exception {
            mockMvc.perform(delete("/api/v1/clients/{id}", CLIENT_ID))
                    .andExpect(status().isForbidden());
        }
    }
}
