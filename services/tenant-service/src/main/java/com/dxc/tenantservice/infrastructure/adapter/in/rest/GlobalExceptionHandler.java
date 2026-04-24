package com.dxc.tenantservice.infrastructure.adapter.in.rest;

import com.dxc.tenantservice.domain.exception.KeycloakIntegrationException;
import com.dxc.tenantservice.domain.exception.TenantStateException;
import com.dxc.tenantservice.domain.exception.UserStateException;
import com.dxc.tenantservice.domain.exception.UserValidationException;
import com.dxc.tenantservice.infrastructure.adapter.in.rest.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("VALIDATION_ERROR", "Invalid request data", details));
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ErrorResponse> handleUserValidation(UserValidationException ex) {
        log.warn("User validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("USER_VALIDATION_ERROR", ex.getMessage(), null));
    }

    @ExceptionHandler(UserStateException.class)
    public ResponseEntity<ErrorResponse> handleUserState(UserStateException ex) {
        log.warn("User state error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of("USER_STATE_ERROR", ex.getMessage(), null));
    }

    @ExceptionHandler(TenantStateException.class)
    public ResponseEntity<ErrorResponse> handleTenantState(TenantStateException ex) {
        log.warn("Tenant state error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of("TENANT_STATE_ERROR", ex.getMessage(), null));
    }

    @ExceptionHandler(KeycloakIntegrationException.class)
    public ResponseEntity<ErrorResponse> handleKeycloak(KeycloakIntegrationException ex) {
        log.error("Keycloak integration error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.of("KEYCLOAK_ERROR", "Identity provider error", null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("BAD_REQUEST", ex.getMessage(), null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGeneric(RuntimeException ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_ERROR", "Something went wrong", null));
    }
}