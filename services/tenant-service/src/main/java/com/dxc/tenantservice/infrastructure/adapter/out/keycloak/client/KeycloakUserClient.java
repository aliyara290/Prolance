package com.dxc.tenantservice.infrastructure.adapter.out.keycloak.client;

import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.config.KeycloakAdminFeignConfig;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users.KeycloakUserReqDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users.KeycloakUserResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "keycloak-admin-api",
        contextId = "keycloak-user-client",
        url = "${keycloak.base-url}",
        configuration = KeycloakAdminFeignConfig.class
)
public interface KeycloakUserClient {
    @PostMapping("/admin/realms/${keycloak.realm}/users")
    ResponseEntity<Void> createUser(@RequestBody KeycloakUserReqDTO user);

    @GetMapping("/admin/realms/${keycloak.realm}/users/{userId}")
    KeycloakUserResDTO getUser(@PathVariable("userId") UUID userId);

    @DeleteMapping("/admin/realms/${keycloak.realm}/users/{userId}")
    void deleteUser(@PathVariable("userId") String userId);

    @PutMapping("/admin/realms/${keycloak.realm}/users/{userId}/groups/{groupId}")
    void addUserToGroup(@PathVariable("userId") String userId, @PathVariable("groupId") UUID groupId);

    @DeleteMapping("/admin/realms/${keycloak.realm}/users/{userId}/groups/{groupId}")
    void removeUserFromGroup(@PathVariable("userId") String userId, @PathVariable("groupId") UUID groupId);

    @PutMapping("/admin/realms/${keycloak.realm}/users/{userId}/execute-actions-email")
    void executeActionsEmail(@PathVariable("userId") String userId, @RequestParam(value = "client_id", required = false) String clientId, @RequestParam(value = "lifespan", required = false) Integer lifespan, @RequestBody List<String> actions);

    @PostMapping("/admin/realms/${keycloak.realm}/users/{userId}/logout")
    void logOutUser(@PathVariable("userId") UUID keycloakUserId);

    @PutMapping("/admin/realms/${keycloak.realm}/users/{userId}")
    void deactivateUser(@PathVariable("userId") UUID keycloakUserId, @RequestBody KeycloakUserResDTO user);

    @PutMapping("/admin/realms/${keycloak.realm}/users/{userId}")
    void activateUser(@PathVariable("userId") UUID keycloakUserId, @RequestBody KeycloakUserResDTO user);
}
