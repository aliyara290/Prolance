package com.dxc.projectservice.application.port.out.feign;

import com.dxc.projectservice.infrastructure.adapter.out.feign.dto.ResponseWrapper;
import com.dxc.projectservice.infrastructure.adapter.out.feign.dto.UserResponseDTO;

import java.util.UUID;

public interface UserFeignPort {
    ResponseWrapper<UserResponseDTO> getUser(UUID id);
}

