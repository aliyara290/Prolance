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
import com.dxc.tenantservice.domain.model.enums.UserRole;
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

    private final TenantPersistenceService tenantPersistenceService;
    private final TenantRepository tenantRepository;
    private final TenantDtoMapper tenantDtoMapper;
    private final ApplicationContext ctx;
    private final KeycloakPort keycloakPort;

//    private static final List<String> DEFAULT_ROLES =
//            List.of("ADMIN", "MEMBER", "VIEWER", "PROJECT_MANAGER", "ACCOUNTANT");

    @Override
    public String registerTenant(RegisterTenantReqDTO reqDTO) {
        RegistrationSaga saga = ctx.getBean(RegistrationSaga.class);
        UUID tenantId = UUID.randomUUID();

        try {
//            register company in keycloak as group
            UUID tenantGroupId = keycloakPort.createCompanyGroup(tenantId);
            saga.register(() -> keycloakPort.deleteGroup(tenantGroupId));

//            create subgroup for each role
            for (UserRole role : UserRole.values()) {
                keycloakPort.createCompanySubGroup(tenantGroupId, role.name());
            }

            KeycloakUserReqDTO userReq = buildKeycloakUser(reqDTO, tenantId);

//            create user in keycloak and assign to company subgroup group (Admin group)
            String keycloakUserIdStr = keycloakPort.createUser(userReq, tenantGroupId);
            saga.register(() -> keycloakPort.deleteUser(keycloakUserIdStr));

            UUID roleGroupId = keycloakPort.assignUserToGroup(keycloakUserIdStr, tenantGroupId, "ADMIN");

            log.info("ABOUT TO PERSIST TO DB");
            // Persist to Database (to rollback if any step fails using Transactions)
            tenantPersistenceService.persist(tenantId, tenantGroupId, roleGroupId, reqDTO, keycloakUserIdStr);

            log.info("Sending email verification to user: {}", reqDTO.getEmail());
            keycloakPort.sendEmailVerificationToUser(keycloakUserIdStr, List.of("VERIFY_EMAIL"));
            return "Tenant registered successfully";
        } catch (Exception ex) {
            log.error("Registration failed for tenant: {}. Initiating rollback...", reqDTO.getName(), ex);
            // rollback if any step failed
            saga.rollback();
            throw new RuntimeException("Failed to register tenant", ex);
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

    private KeycloakUserReqDTO buildKeycloakUser(RegisterTenantReqDTO reqDTO, UUID tenantId) {
        return KeycloakUserReqDTO.builder()
                .username(reqDTO.getEmail())
                .email(reqDTO.getEmail())
                .firstName(reqDTO.getFirstName())
                .lastName(reqDTO.getLastName())
                .enabled(true)
                .emailVerified(false)
                .attributes(Map.of("tenantId", List.of(tenantId.toString())))
                .credentials(List.of(
                        new KeycloakUserReqDTO.KeycloakCredentialRepresentation(
                                "password",
                                reqDTO.getPassword(),
                                false
                        )
                ))
                .build();
    }
}
