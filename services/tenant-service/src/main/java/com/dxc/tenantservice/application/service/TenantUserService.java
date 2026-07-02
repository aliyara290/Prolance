package com.dxc.tenantservice.application.service;

import com.dxc.tenantservice.application.dto.user.req.ChangeUserRoleReqDTO;
import com.dxc.tenantservice.application.dto.user.req.CreateUserReqDTO;
import com.dxc.tenantservice.application.dto.user.req.UpdateUserReqDTO;
import com.dxc.tenantservice.application.dto.user.res.UserResDTO;
import com.dxc.tenantservice.application.mapper.TenantUserDtoMapper;
import com.dxc.tenantservice.application.port.in.TenantUserUseCase;
import com.dxc.tenantservice.application.port.out.TenantRepository;
import com.dxc.tenantservice.application.port.out.TenantUserRepository;
import com.dxc.tenantservice.application.port.out.keycloak.KeycloakPort;
import com.dxc.tenantservice.domain.exception.TenantStateException;
import com.dxc.tenantservice.domain.exception.UserStateException;
import com.dxc.tenantservice.domain.exception.UserValidationException;
import com.dxc.tenantservice.domain.model.tenant.Tenant;
import com.dxc.tenantservice.domain.model.tenant.TenantSettings;
import com.dxc.tenantservice.domain.model.tenant.TenantUser;
import com.dxc.tenantservice.domain.model.tenant.UserPreference;
import com.dxc.tenantservice.domain.model.valueobject.UserRole;
import com.dxc.tenantservice.infrastructure.adapter.out.feign.dto.users.KeycloakUserReqDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.feign.dto.users.KeycloakUserResDTO;
import com.dxc.tenantservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TenantUserService implements TenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantRepository tenantRepository;
    private final KeycloakPort keycloakPort;
    private final TenantUserDtoMapper userDtoMapper;

    @Override
    @Transactional
    public UserResDTO inviteUser(CreateUserReqDTO dto) {
        UUID tenantId = resolveTenantId();
        log.info("Creating user for tenant: tenantId={}, email={}", tenantId, dto.getEmail());

        // Validate email uniqueness within tenant
        if (tenantUserRepository.existsByEmailAndTenantId(dto.getEmail(), tenantId)) {
            throw new UserValidationException("User with email " + dto.getEmail() + " already exists in this tenant");
        }

        // Enforce plan limits
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new TenantStateException("Tenant not found: " + tenantId));

        log.info("================ Tenant found: {}", tenant.getKeycloakGroupId());

        TenantSettings settings = tenant.getTenantSettings();
        if (settings != null) {
            long currentUserCount = tenantUserRepository.countByTenantId(tenantId);
            if (currentUserCount >= settings.getMaxUsers()) {
                throw new UserStateException("Tenant has reached the maximum number of users (" + settings.getMaxUsers() + ") for the current plan");
            }
        }

        // Build Keycloak user request
        KeycloakUserReqDTO keycloakReq = buildKeycloakUser(dto, tenantId);

        // Create user in Keycloak
        UUID tenantGroupId = tenant.getKeycloakGroupId();
        String keycloakUserIdStr = keycloakPort.createUser(keycloakReq, tenantGroupId);
        log.info("User created in Keycloak: keycloakUserId={}", keycloakUserIdStr);

        try {
            // Assign all roles in Keycloak
            UUID keycloakUserId = UUID.fromString(keycloakUserIdStr);
            TenantUser user = TenantUser.create(tenantId, dto.getEmail(), dto.getUsername(), dto.getFirstName(), dto.getLastName());
            user.assignKeycloakUser(keycloakUserId);

            for (UserRole role : dto.getRoles()) {
                UUID roleGroupId = keycloakPort.assignUserToGroup(keycloakUserIdStr, tenantGroupId, role.name());
                user.addRoleGroup(roleGroupId);
            }

            // Set optional profile fields if exit in the DTO
            if (dto.getJobTitle() != null || dto.getDepartment() != null) {
                user.updateProfile(dto.getFirstName(), dto.getLastName(), dto.getJobTitle(), dto.getDepartment());
            }

            // Create default user preferences from tenant settings
            if (settings != null) {
                UserPreference preference = UserPreference.createDefault(tenantId, user.getId(), settings);
                user.addUserPreference(preference);
            }

            TenantUser saved = tenantUserRepository.save(user);
            log.info("User persisted: userId={}, tenantId={}", saved.getId(), tenantId);

            keycloakPort.sendEmailVerificationToUser(keycloakUserIdStr, List.of("VERIFY_EMAIL", "UPDATE_PASSWORD"));
            return userDtoMapper.toDto(saved);

        } catch (Exception ex) {
            // Compensate: delete user from Keycloak if DB persist fails
            log.error("Failed to persist user, compensating Keycloak user: {}", keycloakUserIdStr, ex);
            try {
                keycloakPort.deleteUser(keycloakUserIdStr);
            } catch (Exception compensationEx) {
                log.error("Keycloak compensation failed for user: {}. Manual cleanup required.", keycloakUserIdStr, compensationEx);
            }
            throw new UserStateException("Failed to create user: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public UserResDTO updateUser(UUID userId, UpdateUserReqDTO dto) {
        UUID tenantId = resolveTenantId();
        log.info("Updating user: userId={}, tenantId={}", userId, tenantId);

        TenantUser user = findUserByIdAndTenant(userId, tenantId);

        user.updateProfile(dto.getFirstName(), dto.getLastName(), dto.getJobTitle(), dto.getDepartment());

        TenantUser saved = tenantUserRepository.save(user);
        return userDtoMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deactivateUser(UUID userId) {
        UUID tenantId = resolveTenantId();
        log.info("Deactivating user: userId={}, tenantId={}", userId, tenantId);

        try {
            TenantUser user = findUserByIdAndTenant(userId, tenantId);
            user.deactivate();

            KeycloakUserResDTO keycloakUser = keycloakPort.getUser(user.getKeycloakUserId());
            keycloakUser.setEnabled("false");
            keycloakPort.deactivateUser(user.getKeycloakUserId(), keycloakUser);

            keycloakPort.logOutUser(user.getKeycloakUserId());
            tenantUserRepository.save(user);

            log.info("User deactivated: userId={}", userId);
        } catch (Exception ex) {
            log.error("Failed to deactivate user: {}", userId, ex);
            throw new UserStateException("Failed to deactivate user: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void activateUser(UUID userId) {
        UUID tenantId = resolveTenantId();
        log.info("activating user: userId={}, tenantId={}", userId, tenantId);

        try {
            TenantUser user = findUserByIdAndTenant(userId, tenantId);
            user.activate();

            log.info("User activated: user status={}", user.getStatus());

            KeycloakUserResDTO keycloakUser = keycloakPort.getUser(user.getKeycloakUserId());
            keycloakUser.setEnabled("true");
            keycloakPort.activateUser(user.getKeycloakUserId(), keycloakUser);

            tenantUserRepository.save(user);

            log.info("User activated: userId={}", userId);
        } catch (Exception ex) {
            log.error("Failed to activate user: {}", userId, ex);
            throw new UserStateException("Failed to activate user: " + ex.getMessage());
        }
    }

    @Override
    public UserResDTO getUser(UUID userId) {
        UUID tenantId = resolveTenantId();
        TenantUser user = findUserByIdAndTenant(userId, tenantId);
        return userDtoMapper.toDto(user);
    }

    @Override
    public UserResDTO getUserByKeycloakId() {
        UUID tenantId = resolveTenantId();
        UUID keycloakUserId = TenantContextHolder.getUserId();
        log.debug("Fetching user by keycloakId for tenant: tenantId={}", tenantId);
        if (keycloakUserId == null) {
            throw new UserStateException("Keycloak user ID not found");
        }
        TenantUser user = tenantUserRepository.findByKeycloakUserId(keycloakUserId).orElseThrow(() -> new UserStateException("User not found"));
        return userDtoMapper.toDto(user);
    }

    @Override
    public UserResDTO findUserByKeycloakIdAndTenantId(UUID keycloakUserId) {
        UUID tenantId = resolveTenantId();
        log.debug("Fetching user by keycloakId and tenantId: keycloakUserId={}, tenantId={}", keycloakUserId, tenantId);
        TenantUser user = tenantUserRepository.findByKeycloakUserIdAndTenantId(keycloakUserId, tenantId)
                .orElseThrow(() -> new UserStateException("User not found"));
        return userDtoMapper.toDto(user);
    }

    @Override
    public Page<UserResDTO> getAllUsers(Pageable pageable) {
        UUID tenantId = resolveTenantId();
        log.debug("Fetching all users for tenant: tenantId={}", tenantId);

        return tenantUserRepository.findByTenantId(pageable, tenantId).map(userDtoMapper::toDto);
    }


    @Override
    @Transactional
    public UserResDTO addUserRole(UUID userId, ChangeUserRoleReqDTO dto) {
        UUID tenantId = resolveTenantId();
        UserRole role = dto.getRole();
        log.info("Adding role to user: userId={}, role={}, tenantId={}", userId, role, tenantId);

        TenantUser user = findUserByIdAndTenant(userId, tenantId);

        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new TenantStateException("Tenant not found: " + tenantId));

        // Assign in Keycloak and track the group ID
        UUID roleGroupId = keycloakPort.assignUserToGroup(user.getKeycloakUserId().toString(), tenant.getKeycloakGroupId(), role.name());
        user.addRoleGroup(roleGroupId);

        TenantUser saved = tenantUserRepository.save(user);
        log.info("Role added: userId={}, role={}, roleGroupId={}", userId, role, roleGroupId);

        return userDtoMapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserResDTO removeUserRole(UUID userId, ChangeUserRoleReqDTO dto) {
        UUID tenantId = resolveTenantId();
        UserRole role = dto.getRole();
        log.info("Removing role from user: userId={}, role={}, tenantId={}", userId, role, tenantId);

        TenantUser user = findUserByIdAndTenant(userId, tenantId);

        if (user.getKeycloakRoleGroupIds().size() <= 1) {
            throw new UserStateException("Cannot remove the last role from user. A user must have at least one role.");
        }

        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new TenantStateException("Tenant not found: " + tenantId));

        // Resolve the role group ID before removing from Keycloak
        UUID roleGroupId = keycloakPort.findRoleGroupId(tenant.getKeycloakGroupId(), role.name());

        // Remove from Keycloak
        keycloakPort.removeUserFromGroup(user.getKeycloakUserId().toString(), tenant.getKeycloakGroupId(), role.name());
        user.removeRoleGroup(roleGroupId);

        TenantUser saved = tenantUserRepository.save(user);
        log.info("Role removed: userId={}, role={}", userId, role);

        // Logout user from Keycloak
        log.info("Logging out user: userId={}", userId);
        keycloakPort.logOutUser(user.getKeycloakUserId());
        return userDtoMapper.toDto(saved);
    }

    // Helpers

    private UUID resolveTenantId() {
        String tenantIdStr = TenantContextHolder.getTenantId();
        if (tenantIdStr == null || tenantIdStr.isBlank()) {
            throw new TenantStateException("Tenant context not available. Authentication required.");
        }
        return UUID.fromString(tenantIdStr);
    }

    private TenantUser findUserByIdAndTenant(UUID userId, UUID tenantId) {
        TenantUser user = tenantUserRepository.findById(userId).orElseThrow(() -> new UserStateException("User not found: " + userId));

        if (!user.getTenantId().equals(tenantId)) {
            throw new UserStateException("User not found: " + userId);
        }
        return user;
    }

    private KeycloakUserReqDTO buildKeycloakUser(CreateUserReqDTO dto, UUID tenantId) {
        return KeycloakUserReqDTO.builder().username(dto.getEmail()).email(dto.getEmail()).firstName(dto.getFirstName()).lastName(dto.getLastName()).enabled(true).emailVerified(false).attributes(Map.of("tenantId", List.of(tenantId.toString()))).credentials(List.of(new KeycloakUserReqDTO.KeycloakCredentialRepresentation("password", dto.getPassword(), true))).build();
    }
}
