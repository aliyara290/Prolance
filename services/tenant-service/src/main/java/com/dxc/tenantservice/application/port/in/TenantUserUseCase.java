package com.dxc.tenantservice.application.port.in;

import com.dxc.tenantservice.application.dto.user.req.CreateUserReqDTO;
import com.dxc.tenantservice.application.dto.user.req.UpdateUserReqDTO;
import com.dxc.tenantservice.application.dto.user.res.UserResDTO;

import java.util.List;
import java.util.UUID;

public interface TenantUserUseCase {
    UserResDTO createUser(CreateUserReqDTO createUserReqDTO);
    UserResDTO updateUser(UUID userId, UpdateUserReqDTO updateUserReqDTO);
    UserResDTO deleteUser(UUID userId);
    UserResDTO getUser(UUID userId);
    List<UserResDTO> getAllUsers();
}
