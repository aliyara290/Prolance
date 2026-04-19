package com.dxc.tenantservice.infrastructure.adapter.out.keycloak.client;

import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.config.KeycloakAdminFeignConfig;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.groups.CreateKeycloakGroupReqDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.groups.KeycloakGroupResDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.role.KeycloakRoleReqDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.role.KeycloakRoleResDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users.KeycloakUserResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "keycloak-admin-api",
        contextId = "keycloak-group-client",
        url = "${keycloak.base-url}",
        configuration = KeycloakAdminFeignConfig.class
)
public interface KeycloakGroupClient {

    @PostMapping("/admin/realms/${keycloak.realm}/groups")
    ResponseEntity<Void> createGroup(@RequestBody CreateKeycloakGroupReqDTO group);

    @GetMapping("/admin/realms/${keycloak.realm}/groups/{groupId}")
    KeycloakGroupResDTO getGroup(@PathVariable("groupId") String groupId,
                                 @RequestParam(value = "briefRepresentation", defaultValue = "false") boolean briefRepresentation);

    @PostMapping("/admin/realms/${keycloak.realm}/groups/{groupId}/children")
    ResponseEntity<Void> createSubgroup(@PathVariable("groupId") UUID groupId,
                                        @RequestBody CreateKeycloakGroupReqDTO subgroup);

    @GetMapping("/admin/realms/${keycloak.realm}/groups/{groupId}/members")
    List<KeycloakUserResDTO> getGroupMembers(@PathVariable("groupId") String groupId);

    @GetMapping("/admin/realms/${keycloak.realm}/groups/{groupId}/children")
    List<KeycloakGroupResDTO> getGroupChildren(@PathVariable("groupId") UUID groupId);

    @GetMapping("/admin/realms/${keycloak.realm}/roles/{name}")
    KeycloakRoleResDTO getRealmRole(@PathVariable String name);

    @PostMapping("/admin/realms/${keycloak.realm}/groups/{groupId}/role-mappings/realm")
    ResponseEntity<Void> attachRoleToGroup(@PathVariable String groupId, @RequestBody List<KeycloakRoleReqDTO> roles);
}