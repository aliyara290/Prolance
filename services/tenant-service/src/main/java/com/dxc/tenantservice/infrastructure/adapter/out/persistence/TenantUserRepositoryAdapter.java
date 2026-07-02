package com.dxc.tenantservice.infrastructure.adapter.out.persistence;

import com.dxc.tenantservice.application.port.out.TenantUserRepository;
import com.dxc.tenantservice.domain.model.tenant.TenantUser;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.TenantUserEntity;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.jpa.TenantUserRepositoryJpa;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.mapper.TenantUserPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TenantUserRepositoryAdapter implements TenantUserRepository {

    private final TenantUserRepositoryJpa repository;
    private final TenantUserPersistenceMapper mapper;

    @Override
    public TenantUser save(TenantUser user) {
        log.info("User saved: user status={}", user.getStatus());
        TenantUserEntity entity = mapper.domainToEntity(user);
        TenantUserEntity saved = repository.save(entity);
        log.info("User saved: user status={}", saved.getStatus());
        return mapper.entityToDomain(saved);
    }

    @Override
    public Optional<TenantUser> findById(UUID id) {
        return repository.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public Optional<TenantUser> findByKeycloakUserId(UUID keycloakUserId) {
        return repository.findByKeycloakUserId(keycloakUserId).map(mapper::entityToDomain);
    }

    @Override
    public Optional<TenantUser> findByKeycloakUserIdAndTenantId(UUID keycloakUserId, UUID tenantId) {
        return repository.findByKeycloakUserIdAndTenantId(keycloakUserId, tenantId).map(mapper::entityToDomain);
    }

    @Override
    public Optional<TenantUser> findByEmailAndTenantId(String email, UUID tenantId) {
        return repository.findByEmailAndTenantId(email, tenantId).map(mapper::entityToDomain);
    }

    @Override
    public Page<TenantUser> findByTenantId(Pageable pageable, UUID tenantId) {
        return repository.findByTenantId(pageable, tenantId)
                .map(mapper::entityToDomain);
    }

    @Override
    public boolean existsByEmailAndTenantId(String email, UUID tenantId) {
        return repository.existsByEmailAndTenantId(email, tenantId);
    }

    @Override
    public long countByTenantId(UUID tenantId) {
        return repository.countByTenantId(tenantId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
