package com.dxc.tenantservice.infrastructure.adapter.out.persistence;

import com.dxc.tenantservice.application.port.out.TenantRepository;
import com.dxc.tenantservice.domain.model.tenant.Tenant;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.TenantEntity;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.jpa.TenantRepositoryJpa;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.mapper.TenantPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TenantRepositoryAdapter implements TenantRepository {

    private final TenantRepositoryJpa repository;
    private final TenantPersistenceMapper mapper;

    @Override
    public Tenant save(Tenant tenant) {
        TenantEntity entity = mapper.domainToEntity(tenant);
        TenantEntity saved = repository.save(entity);
        return mapper.entityToDomain(saved);
    }

    @Override
    public Optional<Tenant> findById(UUID id) {
        return repository.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public Optional<Tenant> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::entityToDomain);
    }

    @Override
    public List<Tenant> findAll() {
        return repository.findAll().stream()
                .map(mapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
