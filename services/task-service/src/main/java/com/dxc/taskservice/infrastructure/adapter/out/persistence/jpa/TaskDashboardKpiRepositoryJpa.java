package com.dxc.taskservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.taskservice.infrastructure.adapter.out.persistence.entity.TaskEntity;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository providing aggregate JPQL queries for task
 * dashboard KPIs.
 * All queries operate directly on {@link TaskEntity} and are tenant-scoped.
 */
@Repository
public interface TaskDashboardKpiRepositoryJpa extends JpaRepository<TaskEntity, UUID> {

        // ── Scalar counts ──────────────────────────────────────────────────

        @Query("SELECT COUNT(t) FROM TaskEntity t WHERE t.tenantId = :tenantId")
        long countAllByTenantId(@Param("tenantId") UUID tenantId);

        @Query("SELECT COUNT(t) FROM TaskEntity t WHERE t.tenantId = :tenantId AND t.status = :status")
        long countByTenantIdAndStatus(@Param("tenantId") UUID tenantId, @Param("status") TaskStatus status);

        @Query("""
                        SELECT COUNT(t) FROM TaskEntity t
                        WHERE t.tenantId = :tenantId
                          AND t.status NOT IN ('DONE', 'CANCELLED')
                          AND t.dueDate IS NOT NULL
                          AND t.dueDate < :now
                        """)
        long countOverdue(@Param("tenantId") UUID tenantId, @Param("now") LocalDateTime now);

        @Query("""
                        SELECT COUNT(t) FROM TaskEntity t
                        WHERE t.tenantId = :tenantId
                          AND t.status NOT IN ('DONE', 'CANCELLED')
                          AND t.dueDate BETWEEN :from AND :to
                        """)
        long countDueBetween(@Param("tenantId") UUID tenantId, @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        @Query("""
                        SELECT COUNT(t) FROM TaskEntity t
                        WHERE t.tenantId = :tenantId
                          AND t.status = 'DONE'
                          AND t.completedAt BETWEEN :from AND :to
                        """)
        long countCompletedBetween(@Param("tenantId") UUID tenantId, @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        @Query("SELECT COUNT(t) FROM TaskEntity t WHERE t.tenantId = :tenantId AND t.createdAt BETWEEN :from AND :to")
        long countCreatedBetween(@Param("tenantId") UUID tenantId, @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        @Query(value = """
                        SELECT AVG(EXTRACT(EPOCH FROM (t.completed_at - t.created_at)) / 3600.0)
                        FROM tasks t
                        WHERE t.tenant_id = :tenantId
                          AND t.status = 'DONE'
                          AND t.completed_at IS NOT NULL
                        """, nativeQuery = true)
        Double averageCompletionTimeHours(@Param("tenantId") UUID tenantId);

        // ── Chart datasets ─────────────────────────────────────────────────

        @Query("""
                        SELECT CAST(t.status AS string), COUNT(t)
                        FROM TaskEntity t
                        WHERE t.tenantId = :tenantId
                        GROUP BY t.status
                        ORDER BY t.status
                        """)
        List<Object[]> countGroupedByStatus(@Param("tenantId") UUID tenantId);

        @Query("""
                        SELECT CAST(t.priority AS string), COUNT(t)
                        FROM TaskEntity t
                        WHERE t.tenantId = :tenantId
                        GROUP BY t.priority
                        ORDER BY t.priority
                        """)
        List<Object[]> countGroupedByPriority(@Param("tenantId") UUID tenantId);

        @Query("""
                        SELECT FUNCTION('TO_CHAR', t.createdAt, 'Month'), EXTRACT(YEAR FROM t.createdAt), COUNT(t)
                        FROM TaskEntity t
                        WHERE t.tenantId = :tenantId
                          AND t.createdAt BETWEEN :from AND :to
                        GROUP BY FUNCTION('TO_CHAR', t.createdAt, 'Month'), EXTRACT(YEAR FROM t.createdAt),
                                 FUNCTION('TO_CHAR', t.createdAt, 'YYYY-MM')
                        ORDER BY FUNCTION('TO_CHAR', t.createdAt, 'YYYY-MM')
                        """)
        List<Object[]> countCreatedPerMonth(@Param("tenantId") UUID tenantId, @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        @Query("""
                        SELECT FUNCTION('TO_CHAR', t.completedAt, 'Month'), EXTRACT(YEAR FROM t.completedAt), COUNT(t)
                        FROM TaskEntity t
                        WHERE t.tenantId = :tenantId
                          AND t.status = 'DONE'
                          AND t.completedAt BETWEEN :from AND :to
                        GROUP BY FUNCTION('TO_CHAR', t.completedAt, 'Month'), EXTRACT(YEAR FROM t.completedAt),
                                 FUNCTION('TO_CHAR', t.completedAt, 'YYYY-MM')
                        ORDER BY FUNCTION('TO_CHAR', t.completedAt, 'YYYY-MM')
                        """)
        List<Object[]> countCompletedPerMonth(@Param("tenantId") UUID tenantId, @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        // ── Recent activity ────────────────────────────────────────────────

        @Query("""
                        SELECT t.id, t.projectId, t.title, CAST(t.status AS string), CAST(t.priority AS string), CAST(t.type AS string), t.createdAt, t.completedAt
                        FROM TaskEntity t
                        WHERE t.tenantId = :tenantId
                        ORDER BY t.createdAt DESC
                        LIMIT :limit
                        """)
        List<Object[]> findRecentlyCreated(@Param("tenantId") UUID tenantId, @Param("limit") int limit);

        @Query("""
                        SELECT t.id, t.projectId, t.title, CAST(t.status AS string), CAST(t.priority AS string), CAST(t.type AS string), t.createdAt, t.completedAt
                        FROM TaskEntity t
                        WHERE t.tenantId = :tenantId
                          AND t.status = 'DONE'
                          AND t.completedAt IS NOT NULL
                        ORDER BY t.completedAt DESC
                        LIMIT :limit
                        """)
        List<Object[]> findRecentlyCompleted(@Param("tenantId") UUID tenantId, @Param("limit") int limit);
}
