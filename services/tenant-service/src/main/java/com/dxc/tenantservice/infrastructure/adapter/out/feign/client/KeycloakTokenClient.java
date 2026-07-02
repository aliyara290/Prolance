package com.dxc.tenantservice.infrastructure.adapter.out.feign.client;

import com.dxc.tenantservice.infrastructure.adapter.out.feign.config.KeycloakTokenClientConfig;
import com.dxc.tenantservice.infrastructure.adapter.out.feign.dto.keycloak.KeycloakTokenResDTO;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "keycloak-token-client",
        url = "${keycloak.base-url}",
        configuration = KeycloakTokenClientConfig.class
)
public interface KeycloakTokenClient {

    @RequestLine("POST /realms/{realm}/protocol/openid-connect/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    KeycloakTokenResDTO getAdminToken(
            @Param("realm") String realm,
            @Param("grant_type") String grantType,
            @Param("client_id") String clientId,
            @Param("client_secret") String clientSecret
    );
}
