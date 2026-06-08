package com.dxc.projectservice.infrastructure.adapter.out.feign;

import com.dxc.projectservice.application.port.out.feign.UserFeignPort;
import com.dxc.projectservice.domain.exception.RecordNotFoundException;
import com.dxc.projectservice.infrastructure.adapter.out.feign.client.UserClient;
import com.dxc.projectservice.infrastructure.adapter.out.feign.dto.ResponseWrapper;
import com.dxc.projectservice.infrastructure.adapter.out.feign.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClientAdapter implements UserFeignPort {

    private final UserClient userClient;

    @Override
    public ResponseWrapper<UserResponseDTO> getUser(UUID id) {
        ResponseEntity<ResponseWrapper<UserResponseDTO>> user = userClient.getUser(id);
        if (user.getStatusCode().is4xxClientError()) {
            throw new RecordNotFoundException("Client error while getting user");
        }
        if (user.getStatusCode().is5xxServerError()) {
            throw new RuntimeException("Server error while getting user");
        }
        return user.getBody();
    }
}
