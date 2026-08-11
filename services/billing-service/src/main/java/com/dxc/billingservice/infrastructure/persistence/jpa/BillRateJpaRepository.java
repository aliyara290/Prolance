package com.dxc.billingservice.infrastructure.persistence.jpa;

import com.dxc.billingservice.infrastructure.persistence.entity.BillRateJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillRateJpaRepository extends JpaRepository<BillRateJpaEntity, UUID> {
    Page<BillRateJpaEntity> findByTenantId(UUID tenantId, Pageable pageable);
    List<BillRateJpaEntity> findByProjectIdAndTenantId(UUID projectId, UUID tenantId);
    Optional<BillRateJpaEntity> findByProjectIdAndUserIdAndTenantId(UUID projectId, UUID userId, UUID tenantId);
    Optional<BillRateJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);
    boolean existsByIdAndTenantId(UUID id, UUID tenantId);
    void deleteByIdAndTenantId(UUID id, UUID tenantId);

    @Query("SELECT b FROM BillRateJpaEntity b WHERE b.projectId = :projectId AND b.tenantId = :tenantId " +
            "AND b.effectiveFrom <= :date AND (b.effectiveTo IS NULL OR b.effectiveTo >= :date)")
    List<BillRateJpaEntity> findActiveByProjectIdAndTenantId(
            @Param("projectId") UUID projectId,
            @Param("tenantId") UUID tenantId,
            @Param("date") LocalDate date);
}
