package com.dxc.billingservice.infrastructure.persistence.jpa;

import com.dxc.billingservice.infrastructure.persistence.entity.TimeEntryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeEntryJpaRepository extends JpaRepository<TimeEntryJpaEntity, UUID> {

    List<TimeEntryJpaEntity> findByProjectIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndTenantId(
            UUID projectId, LocalDateTime start, LocalDateTime end, UUID tenantId);

    @Query("SELECT COUNT(t) > 0 FROM TimeEntryJpaEntity t WHERE t.userId = :userId AND t.tenantId = :tenantId " +
           "AND ((t.startTime < :endTime AND t.endTime > :startTime))")
    boolean existsOverlapping(
            @Param("userId") UUID userId, 
            @Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime, 
            @Param("tenantId") UUID tenantId);

    @Query("SELECT COUNT(t) > 0 FROM TimeEntryJpaEntity t WHERE t.userId = :userId AND t.tenantId = :tenantId AND t.id != :entryId " +
           "AND ((t.startTime < :endTime AND t.endTime > :startTime))")
    boolean existsOverlappingExcluding(
            @Param("entryId") UUID entryId,
            @Param("userId") UUID userId, 
            @Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime, 
            @Param("tenantId") UUID tenantId);
}
