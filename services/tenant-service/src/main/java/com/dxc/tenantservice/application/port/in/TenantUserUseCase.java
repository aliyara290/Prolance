package com.dxc.tenantservice.application.port.in;

import com.dxc.tenantservice.application.dto.user.req.ChangeUserRoleReqDTO;
import com.dxc.tenantservice.application.dto.user.req.CreateUserReqDTO;
import com.dxc.tenantservice.application.dto.user.req.UpdateUserReqDTO;
import com.dxc.tenantservice.application.dto.user.res.UserResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TenantUserUseCase {
    UserResDTO inviteUser(CreateUserReqDTO createUserReqDTO);
    UserResDTO updateUser(UUID userId, UpdateUserReqDTO updateUserReqDTO);
    void deactivateUser(UUID userId);
    void activateUser(UUID userId);
    UserResDTO getUser(UUID userId);
    UserResDTO getUserByKeycloakId();
    Page<UserResDTO> getAllUsers(Pageable pageable);
    UserResDTO addUserRole(UUID userId, ChangeUserRoleReqDTO changeUserRoleReqDTO);
    UserResDTO removeUserRole(UUID userId, ChangeUserRoleReqDTO changeUserRoleReqDTO);
}