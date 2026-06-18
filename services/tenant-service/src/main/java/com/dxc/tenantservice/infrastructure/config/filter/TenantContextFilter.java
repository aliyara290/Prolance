package com.dxc.tenantservice.infrastructure.config.filter;

import com.dxc.tenantservice.infrastructure.config.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class TenantContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                String userKeycloakId = jwt.getClaimAsString("sub");
                if (userKeycloakId != null) {
                    TenantContextHolder.setUserId(UUID.fromString(userKeycloakId));
                }
                String tenantId = jwt.getClaimAsString("tenant_id");

                if (tenantId != null) {
                    TenantContextHolder.setTenantId(tenantId);
                }

            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
            TenantContextHolder.clearUserId();
        }
    }
}