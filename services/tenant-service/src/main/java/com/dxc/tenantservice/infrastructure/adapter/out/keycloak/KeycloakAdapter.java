package com.dxc.tenantservice.infrastructure.adapter.out.keycloak;

import com.dxc.tenantservice.application.port.out.keycloak.KeycloakPort;
import com.dxc.tenantservice.domain.exception.KeycloakIntegrationException;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.client.*;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users.KeycloakUserReqDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakAdapter implements KeycloakPort {
    private final KeycloakUserClient userClient;
    private final KeycloakGroupClient groupClient;
    private final KeycloakTokenClient tokenClient;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    @Override
    public String createUser(KeycloakUserReqDTO userDto, UUID tenantId) {
        try {
            log.info("Creating user in Keycloak: email={}, tenantId={}, companyId={}", userDto.getEmail(), tenantId);

//            KeycloakUserReqDTO user = KeycloakUserReqDTO.builder()
//                    .username(email)
//                    .email(email)
//                    .firstName(firstName)
//                    .lastName(lastName)
//                    .enabled(true)
//                    .emailVerified(true)
//                    .attributes(Map.of(
//                            "tenantId", List.of(tenantId.toString())
//                    )).build();

//            KeycloakUserReqDTO.KeycloakCredential credential =
//                    new KeycloakUserRepresentation.KeycloakCredentialRepresentation("password", password, false);
//            user.setCredentials(List.of(credential));

            log.debug("Sending user creation request to Keycloak");
            ResponseEntity<Void> response = userClient.createUser(userDto);

            if (response.getStatusCode().is2xxSuccessful()) {
                String location = response.getHeaders().getLocation().getPath();
                String userId = location.substring(location.lastIndexOf('/') + 1);
                log.info("User created successfully in Keycloak: userId={}", userId);
                return userId;
            } else {
                log.error("Keycloak user creation failed with status: {}", response.getStatusCode());
                throw new KeycloakIntegrationException("Failed to create user in Keycloak: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error creating user in Keycloak: email={}, error={}", userDto.getEmail(), e.getMessage(), e);
            throw new KeycloakIntegrationException("Failed to create user in Keycloak: " + e.getMessage(), e);
        }
    }


    @Override
    public String createUserWithEmailVerification(KeycloakUserReqDTO userDto, UUID tenantId) {
        return "";
    }

    @Override
    public void createCompanyGroup(UUID tenantId) {

    }

    @Override
    public void assignUserToGroup(String keycloakUserId, UUID tenantId, String role) {

    }

    @Override
    public void changeUserGroup(String keycloakUserId, UUID tenantId, String oldRole, String newRole) {

    }

    @Override
    public void removeUserFromGroup(String keycloakUserId, UUID tenantId) {

    }

    @Override
    public void deleteUser(String keycloakUserId) {

    }

    @Override
    public String getUserAccessToken(String email, String password) {
        return "";
    }
}
