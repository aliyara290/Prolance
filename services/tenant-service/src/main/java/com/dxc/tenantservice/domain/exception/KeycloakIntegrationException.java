package com.dxc.tenantservice.domain.exception;

public class KeycloakIntegrationException extends RuntimeException {
    public KeycloakIntegrationException(String message) {
        super("Keycloak integration error: " + message);
    }

    public KeycloakIntegrationException(String message, Throwable cause) {
        super("Keycloak integration error: " + message, cause);
    }
}
