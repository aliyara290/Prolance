package com.dxc.billingservice.infrastructure.adapter.out.persistence;

import com.dxc.billingservice.application.port.out.BillRateRepository;
import com.dxc.billingservice.domain.model.entity.BillRate;
import com.dxc.billingservice.infrastructure.persistence.entity.BillRateJpaEntity;
import com.dxc.billingservice.infrastructure.persistence.jpa.BillRateJpaRepository;
import com.dxc.billingservice.infrastructure.persistence.mapper.PersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BillRateRepositoryAdapter implements BillRateRepository {

    private final BillRateJpaRepository jpaRepository;
    private final PersistenceMapper mapper;

    @Override
    public BillRate save(BillRate billRate) {
        BillRateJpaEntity entity = mapper.toBillRateJpaEntity(billRate);
        BillRateJpaEntity saved = jpaRepository.save(entity);
        return mapper.toBillRateDomain(saved);
    }

    @Override
    public Optional<BillRate> findById(UUID id, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).map(entity -> mapper.toBillRateDomain(entity));
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        jpaRepository.deleteByIdAndTenantId(id, tenantId);
    }

    @Override
    public Page<BillRate> findAll(UUID tenantId, Pageable pageable) {
        return jpaRepository.findByTenantId(tenantId, pageable).map(entity -> mapper.toBillRateDomain(entity));
    }

    @Override
    public boolean existsById(UUID id, UUID tenantId) {
        return jpaRepository.existsByIdAndTenantId(id, tenantId);
    }

    @Override
    public List<BillRate> findByProjectId(UUID projectId, UUID tenantId) {
        return jpaRepository.findByProjectIdAndTenantId(projectId, tenantId)
                .stream().map(entity -> mapper.toBillRateDomain(entity)).collect(Collectors.toList());
    }

    @Override
    public Optional<BillRate> findByProjectIdAndUserId(UUID projectId, UUID userId, UUID tenantId) {
        return jpaRepository.findByProjectIdAndUserIdAndTenantId(projectId, userId, tenantId).map(entity -> mapper.toBillRateDomain(entity));
    }

    @Override
    public List<BillRate> findActiveByProjectId(UUID projectId, UUID tenantId, LocalDate date) {
        return jpaRepository.findActiveByProjectIdAndTenantId(projectId, tenantId, date)
                .stream().map(entity -> mapper.toBillRateDomain(entity)).collect(Collectors.toList());
    }
}
