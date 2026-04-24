package com.dxc.tenantservice.application.service;

import com.dxc.tenantservice.application.dto.tenant.req.RegisterTenantReqDTO;
import com.dxc.tenantservice.application.port.out.TenantRepository;
import com.dxc.tenantservice.application.port.out.TenantUserRepository;
import com.dxc.tenantservice.domain.exception.TenantStateException;
import com.dxc.tenantservice.domain.model.tenant.Tenant;
import com.dxc.tenantservice.domain.model.tenant.TenantSettings;
import com.dxc.tenantservice.domain.model.tenant.TenantUser;
import com.dxc.tenantservice.domain.model.tenant.UserPreference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TenantPersistenceService {

    private final TenantRepository tenantRepository;
    private final TenantUserRepository tenantUserRepository;

    @Transactional
    protected void persist(UUID tenantId, UUID tenantGroupId, UUID roleGroupId, RegisterTenantReqDTO reqDTO, String keycloakUserIdStr) {
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
            user.addRoleGroup(roleGroupId);
            UserPreference userPreference = UserPreference.createDefault(tenantId, user.getId(), tenantSettings);
            user.addUserPreference(userPreference);
            tenantUserRepository.save(user);

        } catch (Exception ex) {
            log.error("Failed to persist tenant and user", ex);
            throw new TenantStateException("Failed to persist tenant and user" + ex);
        }
    }
}
