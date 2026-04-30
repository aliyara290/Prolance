package com.dxc.crmservice.infrastructure.adapter.in.rest.controller;

import com.dxc.crmservice.application.dto.activity.req.CreateActivityRequest;
import com.dxc.crmservice.application.dto.activity.res.ActivityResponse;
import com.dxc.crmservice.application.port.in.ActivityUseCase;
import com.dxc.crmservice.domain.model.valueobject.ActivityType;
import com.dxc.crmservice.domain.model.valueobject.EntityType;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-layer slice test for ActivityController.
 *
 * Excludes SecurityConfig (which needs Keycloak/JwtDecoder) and substitutes a
 * minimal TestSecurityConfig that permits all HTTP requests but enables
 * method security for @PreAuthorize evaluation.
 * @WithMockUser provides the granted authorities for role-based tests.
 */
@WebMvcTest(
        controllers = ActivityController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
@Import(ActivityControllerTest.TestSecurityConfig.class)
@DisplayName("ActivityController – REST Adapter")
class ActivityControllerTest {

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

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean ActivityUseCase activityUseCase;

    private static final UUID ACTIVITY_ID = UUID.randomUUID();
    private static final UUID USER_ID     = UUID.randomUUID();
    private static final UUID ENTITY_ID   = UUID.randomUUID();

    // helpers

    private ActivityResponse stubResponse() {
        return new ActivityResponse(ACTIVITY_ID, ActivityType.CALL, "Follow-up",
                "desc", LocalDateTime.now().plusDays(1), null,
                USER_ID, ENTITY_ID, EntityType.LEAD, LocalDateTime.now(), LocalDateTime.now());
    }

    private CreateActivityRequest createRequest() {
        return new CreateActivityRequest(ActivityType.CALL, "Follow-up", "desc",
                LocalDateTime.now().plusDays(1), USER_ID, ENTITY_ID, EntityType.LEAD);
    }

    @Nested
    @DisplayName("POST /api/v1/activities")
    class CreateActivity {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can create → 201")
        void adminCreates() throws Exception {
            when(activityUseCase.createActivity(any())).thenReturn(stubResponse());

            mockMvc.perform(post("/api/v1/activities")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.subject").value("Follow-up"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER is forbidden → 403")
        void userForbidden() throws Exception {
            mockMvc.perform(post("/api/v1/activities")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest())))
                    .andExpect(status().isForbidden());

        }
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    @DisplayName("PROJECT_MANAGER can read activity → 200")
    void getActivity() throws Exception {
        when(activityUseCase.getActivity(ACTIVITY_ID)).thenReturn(stubResponse());

        mockMvc.perform(get("/api/v1/activities/{id}", ACTIVITY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(ACTIVITY_ID.toString()));
    }

    @Nested
    @DisplayName("DELETE /api/v1/activities/{id}")
    class DeleteActivity {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can delete → 200")
        void adminDeletes() throws Exception {
            doNothing().when(activityUseCase).deleteActivity(ACTIVITY_ID);

            mockMvc.perform(delete("/api/v1/activities/{id}", ACTIVITY_ID))
                    .andExpect(status().isOk());

            verify(activityUseCase).deleteActivity(ACTIVITY_ID);
        }

        @Test
        @WithMockUser(roles = "SALES")
        @DisplayName("SALES is forbidden on DELETE → 403")
        void salesForbidden() throws Exception {
            mockMvc.perform(delete("/api/v1/activities/{id}", ACTIVITY_ID))
                    .andExpect(status().isForbidden());

        }
    }

    @Test
    @WithMockUser(roles = "SALES")
    @DisplayName("PATCH /{id}/complete → 200")
    void completeActivity() throws Exception {
        when(activityUseCase.completeActivity(ACTIVITY_ID)).thenReturn(stubResponse());

        mockMvc.perform(patch("/api/v1/activities/{id}/complete", ACTIVITY_ID))
                .andExpect(status().isOk());

        verify(activityUseCase).completeActivity(ACTIVITY_ID);
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    @DisplayName("GET / returns paginated list → 200")
    void getAllActivities() throws Exception {
        when(activityUseCase.getAllActivities(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(stubResponse())));

        mockMvc.perform(get("/api/v1/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].subject").value("Follow-up"));
    }
}
