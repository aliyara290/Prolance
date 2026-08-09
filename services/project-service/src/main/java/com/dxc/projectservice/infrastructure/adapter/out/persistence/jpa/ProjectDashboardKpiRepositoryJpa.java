package com.dxc.projectservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.ProjectEntity;
import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository providing aggregate JPQL queries for project dashboard KPIs.
 * All queries operate directly on {@link ProjectEntity} and are tenant-scoped.
 */
@Repository
public interface ProjectDashboardKpiRepositoryJpa extends JpaRepository<ProjectEntity, UUID> {

    // ── Scalar counts ──────────────────────────────────────────────────

    @Query("SELECT COUNT(p) FROM ProjectEntity p WHERE p.tenantId = :tenantId")
    long countAllByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT COUNT(p) FROM ProjectEntity p WHERE p.tenantId = :tenantId AND p.status = :status")
    long countByTenantIdAndStatus(@Param("tenantId") UUID tenantId, @Param("status") ProjectStatus status);

    @Query("""
            SELECT COUNT(p) FROM ProjectEntity p
            WHERE p.tenantId = :tenantId
              AND p.status IN ('ACTIVE', 'PLANNED')
              AND p.plannedEndDate IS NOT NULL
              AND p.plannedEndDate < :now
              AND (p.actualEndDate IS NULL)
            """)
    long countOverdue(@Param("tenantId") UUID tenantId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(p) FROM ProjectEntity p WHERE p.tenantId = :tenantId AND p.createdAt BETWEEN :from AND :to")
    long countCreatedBetween(@Param("tenantId") UUID tenantId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
            SELECT COUNT(p) FROM ProjectEntity p
            WHERE p.tenantId = :tenantId
              AND p.status = 'COMPLETED'
              AND p.actualEndDate BETWEEN :from AND :to
            """)
    long countCompletedBetween(@Param("tenantId") UUID tenantId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // ── Chart datasets ─────────────────────────────────────────────────

    @Query("""
            SELECT CAST(p.status AS string), COUNT(p)
            FROM ProjectEntity p
            WHERE p.tenantId = :tenantId
            GROUP BY p.status
            ORDER BY p.status
            """)
    List<Object[]> countGroupedByStatus(@Param("tenantId") UUID tenantId);

    @Query("""
            SELECT FUNCTION('TO_CHAR', p.createdAt, 'Month'), EXTRACT(YEAR FROM p.createdAt), COUNT(p)
            FROM ProjectEntity p
            WHERE p.tenantId = :tenantId
              AND p.createdAt BETWEEN :from AND :to
            GROUP BY FUNCTION('TO_CHAR', p.createdAt, 'Month'), EXTRACT(YEAR FROM p.createdAt),
                     FUNCTION('TO_CHAR', p.createdAt, 'YYYY-MM')
            ORDER BY FUNCTION('TO_CHAR', p.createdAt, 'YYYY-MM')
            """)
    List<Object[]> countCreatedPerMonth(@Param("tenantId") UUID tenantId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
            SELECT FUNCTION('TO_CHAR', p.actualEndDate, 'Month'), EXTRACT(YEAR FROM p.actualEndDate), COUNT(p)
            FROM ProjectEntity p
            WHERE p.tenantId = :tenantId
              AND p.status = 'COMPLETED'
              AND p.actualEndDate BETWEEN :from AND :to
            GROUP BY FUNCTION('TO_CHAR', p.actualEndDate, 'Month'), EXTRACT(YEAR FROM p.actualEndDate),
                     FUNCTION('TO_CHAR', p.actualEndDate, 'YYYY-MM')
            ORDER BY FUNCTION('TO_CHAR', p.actualEndDate, 'YYYY-MM')
            """)
    List<Object[]> countCompletedPerMonth(@Param("tenantId") UUID tenantId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // ── Recent activity ────────────────────────────────────────────────

    @Query("""
            SELECT p.id, p.name, p.prefix, CAST(p.status AS string), CAST(p.priority AS string), p.createdAt, p.actualEndDate
            FROM ProjectEntity p
            WHERE p.tenantId = :tenantId
            ORDER BY p.createdAt DESC
            LIMIT :limit
            """)
    List<Object[]> findRecentlyCreated(@Param("tenantId") UUID tenantId, @Param("limit") int limit);

    @Query("""
            SELECT p.id, p.name, p.prefix, CAST(p.status AS string), CAST(p.priority AS string), p.createdAt, p.actualEndDate
            FROM ProjectEntity p
            WHERE p.tenantId = :tenantId
              AND p.status = 'COMPLETED'
              AND p.actualEndDate IS NOT NULL
            ORDER BY p.actualEndDate DESC
            LIMIT :limit
            """)
    List<Object[]> findRecentlyCompleted(@Param("tenantId") UUID tenantId, @Param("limit") int limit);
}
