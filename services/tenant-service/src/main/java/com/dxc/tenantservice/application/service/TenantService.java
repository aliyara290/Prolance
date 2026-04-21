package com.dxc.tenantservice.application.service;

import com.dxc.tenantservice.application.dto.tenant.req.RegisterTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.req.UpdateTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantResDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantTokenResDTO;
import com.dxc.tenantservice.application.mapper.TenantDtoMapper;
import com.dxc.tenantservice.application.port.in.TenantUseCase;
import com.dxc.tenantservice.application.port.out.TenantRepository;
import com.dxc.tenantservice.application.port.out.TenantUserRepository;
import com.dxc.tenantservice.application.saga.RegistrationSaga;
import com.dxc.tenantservice.application.port.out.keycloak.KeycloakPort;
import com.dxc.tenantservice.domain.exception.TenantStateException;
import com.dxc.tenantservice.domain.model.tenant.UserPreference;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.keycloak.KeycloakTokenResDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users.KeycloakUserReqDTO;
import com.dxc.tenantservice.domain.model.tenant.Tenant;
import com.dxc.tenantservice.domain.model.tenant.TenantUser;
import com.dxc.tenantservice.domain.model.tenant.TenantSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class TenantService implements TenantUseCase {

    private final TenantRepository tenantRepository;
    private final TenantUserRepository tenantUserRepository;
    private final TenantDtoMapper tenantDtoMapper;
    private final ApplicationContext ctx;
    private final KeycloakPort keycloakPort;

    private static final List<String> DEFAULT_ROLES =
            List.of("ADMIN", "MEMBER", "VIEWER", "PROJECT_MANAGER", "ACCOUNTANT");

    @Override
    public TenantTokenResDTO registerTenant(RegisterTenantReqDTO reqDTO) {
        RegistrationSaga saga = ctx.getBean(RegistrationSaga.class);
        UUID tenantId = UUID.randomUUID();

        try {
//            register company in keycloak as group
            UUID tenantGroupId = keycloakPort.createCompanyGroup(tenantId);
            saga.register(() -> keycloakPort.deleteGroup(tenantGroupId));

//            create subgroup for each role
            for (String role : DEFAULT_ROLES) {
                keycloakPort.createCompanySubGroup(tenantGroupId, role);
            }

            KeycloakUserReqDTO userReq = KeycloakUserReqDTO.builder()
                    .username(reqDTO.getEmail())
                    .email(reqDTO.getEmail())
                    .firstName(reqDTO.getFirstName())
                    .lastName(reqDTO.getLastName())
                    .enabled(true)
                    .emailVerified(false)
                    .attributes(Map.of("tenantId", List.of(tenantId.toString())))
                    .build();

//            create user in keycloak and assign to company subgroup group (Admin group)
            String keycloakUserIdStr = keycloakPort.createUser(userReq, tenantGroupId);
            saga.register(() -> keycloakPort.deleteUser(keycloakUserIdStr));

            UUID roleGroupId = keycloakPort.assignUserToGroup(keycloakUserIdStr, tenantGroupId, "ADMIN");

            // Persist to Database (to rollback if any step fails using Transactions)
            persistToDatabase(tenantId, tenantGroupId, roleGroupId, reqDTO, keycloakUserIdStr);

            KeycloakTokenResDTO UserToken = keycloakPort.getUserAccessToken(reqDTO.getEmail(), reqDTO.getPassword());

            TenantTokenResDTO response = TenantTokenResDTO.builder()
                    .accessToken(UserToken.getAccessToken())
                    .refreshToken(UserToken.getRefreshToken())
                    .expiresIn(UserToken.getExpiresIn())
                    .tokenType(UserToken.getTokenType())
                    .build();

            return response;
        } catch (Exception ex) {
            log.error("Registration failed for tenant: {}. Initiating rollback...", reqDTO.getName(), ex);
            // rollback if any step failed
            saga.rollback();
            throw new RuntimeException("Failed to register tenant", ex);
        }
    }

    @Transactional
    protected void persistToDatabase(UUID tenantId, UUID tenantGroupId, UUID roleGroupId, RegisterTenantReqDTO reqDTO, String keycloakUserIdStr) {
        UUID keycloakUserId = UUID.fromString(keycloakUserIdStr);

        try {
            // Persist Tenant
            Tenant tenant = Tenant.create(tenantId, reqDTO.getName(), reqDTO.getEmail(), reqDTO.getIndustry());
            tenant.assignKeycloakGroup(tenantGroupId);
            TenantSettings tenantSettings = TenantSettings.createDefault(tenantId);
            tenant.addSettings(tenantSettings);
            tenantRepository.save(tenant);

            // Persist Tenant Admin user
            TenantUser user = TenantUser.create(tenantId, reqDTO.getEmail(), reqDTO.getFirstName(), reqDTO.getLastName());
            user.assignKeycloakUser(keycloakUserId);
            user.assignToGroup(roleGroupId);
            UserPreference userPreference = UserPreference.createDefault(tenantId, user.getId(), tenantSettings);
            tenantUserRepository.save(user, userPreference);

        } catch (Exception ex) {
            log.error("Failed to persist tenant and user", ex);
            throw new TenantStateException("Failed to persist tenant and user" + ex);
        }
    }

    @Override
    @Transactional
    public TenantResDTO updateTenant(UUID tenantId, UpdateTenantReqDTO reqDTO) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantStateException("Tenant not found with ID: " + tenantId));

        tenant.updateProfile(
                reqDTO.getName(),
                reqDTO.getWebsite(),
                reqDTO.getDescription(),
                reqDTO.getLogo(),
                reqDTO.getAddress(),
                reqDTO.getSize(),
                reqDTO.getFoundedDate()
        );

        tenantRepository.save(tenant);
        return tenantDtoMapper.toDto(tenant);
    }

    @Override
    @Transactional
    public void deleteTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantStateException("Tenant not found with ID: " + tenantId));

        tenant.delete();
        tenantRepository.save(tenant);
    }

    @Override
    public TenantResDTO getTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantStateException("Tenant not found with ID: " + tenantId));

        return tenantDtoMapper.toDto(tenant);
    }
}
