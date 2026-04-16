package com.dxc.tenantservice.infrastructure.adapter.out.persistence;

import com.dxc.tenantservice.application.port.out.TenantUserRepository;
import com.dxc.tenantservice.domain.model.tenant.TenantUser;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.TenantUserEntity;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.jpa.TenantUserRepositoryJpa;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.mapper.TenantUserPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TenantUserRepositoryAdapter implements TenantUserRepository {

    private final TenantUserRepositoryJpa repository;
    private final TenantUserPersistenceMapper mapper;

    @Override
    public TenantUser save(TenantUser user) {
        TenantUserEntity entity = mapper.domainToEntity(user);
        TenantUserEntity saved = repository.save(entity);
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
    public Optional<TenantUser> findByEmailAndTenantId(String email, UUID tenantId) {
        return repository.findByEmailAndTenantId(email, tenantId).map(mapper::entityToDomain);
    }

    @Override
    public List<TenantUser> findByTenantId(UUID tenantId) {
        return repository.findByTenantId(tenantId).stream()
                .map(mapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmailAndTenantId(String email, UUID tenantId) {
        return repository.existsByEmailAndTenantId(email, tenantId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
