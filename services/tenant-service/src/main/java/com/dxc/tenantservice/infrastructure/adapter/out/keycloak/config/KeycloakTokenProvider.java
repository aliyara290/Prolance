package com.dxc.tenantservice.infrastructure.adapter.out.keycloak.config;


import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.client.KeycloakTokenClient;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.keycloak.KeycloakTokenResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakTokenProvider {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    private final KeycloakTokenClient tokenClient;

    private String cachedToken;
    private Instant tokenExpiryTime;


    public synchronized String getValidToken() {
        if (cachedToken == null || tokenExpiryTime == null || Instant.now().isAfter(tokenExpiryTime)) {
            refreshToken();
        }
        log.info("Returning cached token");
        return cachedToken;
    }

    private void refreshToken() {
        try {
            log.info("Refreshing Keycloak admin token for realm: {}", realm);
            KeycloakTokenResDTO response = tokenClient.getAdminToken(
                    realm,
                    "client_credentials",
                    clientId,
                    clientSecret
            );

            this.cachedToken = response.getAccessToken();
            this.tokenExpiryTime = Instant.now().plusSeconds(response.getExpiresIn() - 60);
            log.info("Keycloak admin token refreshed successfully, expires in {} seconds", response.getExpiresIn());
        } catch (Exception e) {
            log.error("Failed to obtain Keycloak admin token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to obtain Keycloak admin token: " + e.getMessage(), e);
        }
    }
}