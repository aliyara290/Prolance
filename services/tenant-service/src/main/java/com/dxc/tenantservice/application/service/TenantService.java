package com.dxc.tenantservice.application.service;

import com.dxc.tenantservice.application.dto.tenant.req.RegisterTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.req.UpdateTenantReqDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantRegistrationResponseDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantResDTO;
import com.dxc.tenantservice.application.dto.tenant.res.TenantStatusRespDTO;
import com.dxc.tenantservice.application.mapper.TenantAddressMapper;
import com.dxc.tenantservice.application.mapper.TenantDtoMapper;
import com.dxc.tenantservice.application.port.in.TenantUseCase;
import com.dxc.tenantservice.application.port.out.TenantRepository;
import com.dxc.tenantservice.application.saga.RegistrationSaga;
import com.dxc.tenantservice.application.port.out.keycloak.KeycloakPort;
import com.dxc.tenantservice.domain.exception.TenantStateException;
import com.dxc.tenantservice.domain.model.valueobject.UserRole;
import com.dxc.tenantservice.domain.model.valueobject.Address;
import com.dxc.tenantservice.domain.model.tenant.Tenant;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users.KeycloakUserResDTO;
import com.dxc.tenantservice.infrastructure.config.TenantContextHolder;
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
    private final TenantAddressMapper tenantAddressMapper;
    private final ApplicationContext ctx;
    private final KeycloakPort keycloakPort;

    @Override
    public TenantRegistrationResponseDTO registerTenant(RegisterTenantReqDTO reqDTO) {
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

            KeycloakUserResDTO userKeycloakRes = keycloakPort.getUser(TenantContextHolder.getUserId());

            log.info("User keycloak id |||||: {}", userKeycloakRes.toString());

            KeycloakUserResDTO userKeycloakReq = new KeycloakUserResDTO();
            userKeycloakReq.setFirstName(userKeycloakRes.getFirstName());
            userKeycloakReq.setLastName(userKeycloakRes.getLastName());
            userKeycloakReq.setEmail(userKeycloakRes.getEmail());
            userKeycloakReq.setAttributes(Map.of("tenantId",List.of(tenantId.toString())));
            keycloakPort.updateUser(userKeycloakRes.getId(), userKeycloakReq);
            saga.register(() -> keycloakPort.updateUser(userKeycloakRes.getId(), userKeycloakRes));

            UUID roleGroupId = keycloakPort.assignUserToGroup(userKeycloakRes.getId().toString(), tenantGroupId, "ADMIN");
            saga.register(() -> keycloakPort.removeUserFromGroup(userKeycloakRes.getId().toString(), tenantGroupId, "ADMIN"));

            log.info("ABOUT TO PERSIST TO DB");
            // Persist to Database (to rollback if any step fails using Transactions)
            tenantPersistenceService.persist(tenantId, tenantGroupId, roleGroupId, reqDTO, userKeycloakRes);
            return new TenantRegistrationResponseDTO(tenantId.toString());
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
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new TenantStateException("Tenant not found with ID: " + tenantId));

        Address address = tenantAddressMapper.toDomain(reqDTO.getAddress());
        tenant.updateProfile(reqDTO.getName(), reqDTO.getWebsite(), reqDTO.getDescription(), reqDTO.getLogo(), address, reqDTO.getSize(), reqDTO.getFoundedDate());

        tenantRepository.save(tenant);
        return tenantDtoMapper.toDto(tenant);
    }

    @Override
    @Transactional
    public void deleteTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new TenantStateException("Tenant not found with ID: " + tenantId));
        tenant.delete();
        tenantRepository.save(tenant);
    }

    @Override
    public TenantResDTO getTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new TenantStateException("Tenant not found with ID: " + tenantId));

        return tenantDtoMapper.toDto(tenant);
    }

    @Override
    public TenantStatusRespDTO getTenantStatus(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new TenantStateException("Tenant not found with ID: " + tenantId));
        log.info("Tenant status ||||||---: {}", tenant.toString());
        return TenantStatusRespDTO.builder().tenantId(tenantId).status(tenant.getStatus().toString()).active(tenant.getDeleted_at() == null).deletedAt(tenant.getDeleted_at()).build();

    }

}
