package com.dxc.crmservice.infrastructure.adapter.in.rest.controller;

import com.dxc.crmservice.application.dto.contact.req.CreateContactRequest;
import com.dxc.crmservice.application.dto.contact.req.UpdateContactRequest;
import com.dxc.crmservice.application.dto.contact.res.ContactResponse;
import com.dxc.crmservice.application.port.in.ContactUseCase;
import com.dxc.crmservice.domain.model.valueobject.InfluenceLevel;
import com.dxc.crmservice.domain.model.valueobject.Role;
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
        controllers = ContactController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
@Import(ContactControllerTest.TestSecurityConfig.class)
@DisplayName("ContactController – REST Adapter")
class ContactControllerTest {

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

    @MockBean ContactUseCase contactUseCase;

    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final UUID CLIENT_ID  = UUID.randomUUID();

    // helpers

    private ContactResponse stubResponse() {
        return new ContactResponse(CONTACT_ID, "John", "Doe",
                "john@acme.com", "+33600000000", Role.CEO, InfluenceLevel.HIGH,
                true, "Key contact", CLIENT_ID, null,
                LocalDateTime.now(), LocalDateTime.now());
    }

    private CreateContactRequest createRequest() {
        return CreateContactRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@acme.com")
                .phone("+33600000000")
                .role(Role.CEO)
                .influenceLevel(InfluenceLevel.HIGH)
                .primary(true)
                .notes("Key contact")
                .clientId(CLIENT_ID)
                .build();
    }

    // ── POST /api/v1/contacts ─────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/v1/contacts")
    class CreateContact {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can create contact → 201")
        void adminCreates() throws Exception {
            when(contactUseCase.createContact(any())).thenReturn(stubResponse());

            mockMvc.perform(post("/api/v1/contacts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.firstName").value("John"));
        }

        @Test
        @WithMockUser(roles = "SALES")
        @DisplayName("SALES can create contact → 201")
        void salesCreates() throws Exception {
            when(contactUseCase.createContact(any())).thenReturn(stubResponse());

            mockMvc.perform(post("/api/v1/contacts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest())))
                    .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER is forbidden → 403")
        void userForbidden() throws Exception {
            mockMvc.perform(post("/api/v1/contacts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest())))
                    .andExpect(status().isForbidden());
        }
    }

    // ── PUT /api/v1/contacts/{id} ─────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /api/v1/contacts/{id}")
    class UpdateContact {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can update → 200")
        void adminUpdates() throws Exception {
            when(contactUseCase.updateContact(eq(CONTACT_ID), any())).thenReturn(stubResponse());

            UpdateContactRequest req = new UpdateContactRequest("Jane", null, null, null, null, null, false, null, null);
            mockMvc.perform(put("/api/v1/contacts/{id}", CONTACT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(CONTACT_ID.toString()));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER is forbidden → 403")
        void userForbidden() throws Exception {
            UpdateContactRequest req = new UpdateContactRequest("Jane", null, null, null, null, null, false, null, null);
            mockMvc.perform(put("/api/v1/contacts/{id}", CONTACT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isForbidden());
        }
    }

    // ── GET /api/v1/contacts/{id} ─────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/contacts/{id}")
    class GetContact {

        @Test
        @WithMockUser(roles = "SALES")
        @DisplayName("SALES can read → 200")
        void salesReads() throws Exception {
            when(contactUseCase.getContact(CONTACT_ID)).thenReturn(stubResponse());

            mockMvc.perform(get("/api/v1/contacts/{id}", CONTACT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.firstName").value("John"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER can read → 200")
        void userReads() throws Exception {
            when(contactUseCase.getContact(CONTACT_ID)).thenReturn(stubResponse());

            mockMvc.perform(get("/api/v1/contacts/{id}", CONTACT_ID))
                    .andExpect(status().isOk());
        }
    }

    // ── GET /api/v1/contacts ──────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET / returns paginated list → 200")
    void getAllContacts() throws Exception {
        when(contactUseCase.getAllContacts(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(stubResponse())));

        mockMvc.perform(get("/api/v1/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].firstName").value("John"));
    }

    // ── DELETE /api/v1/contacts/{id} ──────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/v1/contacts/{id}")
    class DeleteContact {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can delete → 200")
        void adminDeletes() throws Exception {
            doNothing().when(contactUseCase).deleteContact(CONTACT_ID);

            mockMvc.perform(delete("/api/v1/contacts/{id}", CONTACT_ID))
                    .andExpect(status().isOk());

            verify(contactUseCase).deleteContact(CONTACT_ID);
        }

        @Test
        @WithMockUser(roles = "SALES")
        @DisplayName("SALES is forbidden on DELETE → 403")
        void salesForbidden() throws Exception {
            mockMvc.perform(delete("/api/v1/contacts/{id}", CONTACT_ID))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER is forbidden on DELETE → 403")
        void userForbidden() throws Exception {
            mockMvc.perform(delete("/api/v1/contacts/{id}", CONTACT_ID))
                    .andExpect(status().isForbidden());
        }
    }
}
