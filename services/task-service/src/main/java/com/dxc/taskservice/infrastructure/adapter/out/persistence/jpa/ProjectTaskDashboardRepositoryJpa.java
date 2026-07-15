package com.dxc.taskservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.taskservice.infrastructure.adapter.out.persistence.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectTaskDashboardRepositoryJpa extends JpaRepository<TaskEntity, UUID> {

    // ── Scalar counts ──────────────────────────────────────────────────

    @Query(value = """
            SELECT COUNT(*)
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND (CAST(:startDate AS timestamp) IS NULL OR t.created_at >= CAST(:startDate AS timestamp))
              AND (CAST(:endDate AS timestamp) IS NULL OR t.created_at <= CAST(:endDate AS timestamp))
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:priority AS varchar) IS NULL OR t.priority = CAST(:priority AS varchar))
              AND (CAST(:status AS varchar) IS NULL OR t.status = CAST(:status AS varchar))
            """, nativeQuery = true)
    long countAll(@Param("tenantId") UUID tenantId,
                  @Param("projectId") UUID projectId,
                  @Param("startDate") LocalDateTime startDate,
                  @Param("endDate") LocalDateTime endDate,
                  @Param("assigneeId") UUID assigneeId,
                  @Param("priority") String priority,
                  @Param("status") String status);

    @Query(value = """
            SELECT COUNT(*)
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND t.status = CAST(:targetStatus AS varchar)
              AND (CAST(:startDate AS timestamp) IS NULL OR t.created_at >= CAST(:startDate AS timestamp))
              AND (CAST(:endDate AS timestamp) IS NULL OR t.created_at <= CAST(:endDate AS timestamp))
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:priority AS varchar) IS NULL OR t.priority = CAST(:priority AS varchar))
            """, nativeQuery = true)
    long countByStatus(@Param("tenantId") UUID tenantId,
                       @Param("projectId") UUID projectId,
                       @Param("targetStatus") String targetStatus,
                       @Param("startDate") LocalDateTime startDate,
                       @Param("endDate") LocalDateTime endDate,
                       @Param("assigneeId") UUID assigneeId,
                       @Param("priority") String priority);

    @Query(value = """
            SELECT COUNT(*)
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND t.status NOT IN ('DONE', 'CANCELLED')
              AND t.due_date IS NOT NULL
              AND t.due_date < CAST(:now AS timestamp)
              AND (CAST(:startDate AS timestamp) IS NULL OR t.created_at >= CAST(:startDate AS timestamp))
              AND (CAST(:endDate AS timestamp) IS NULL OR t.created_at <= CAST(:endDate AS timestamp))
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:priority AS varchar) IS NULL OR t.priority = CAST(:priority AS varchar))
            """, nativeQuery = true)
    long countOverdue(@Param("tenantId") UUID tenantId,
                      @Param("projectId") UUID projectId,
                      @Param("now") LocalDateTime now,
                      @Param("startDate") LocalDateTime startDate,
                      @Param("endDate") LocalDateTime endDate,
                      @Param("assigneeId") UUID assigneeId,
                      @Param("priority") String priority);

    @Query(value = """
            SELECT COUNT(*)
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND t.priority = CAST(:targetPriority AS varchar)
              AND (CAST(:startDate AS timestamp) IS NULL OR t.created_at >= CAST(:startDate AS timestamp))
              AND (CAST(:endDate AS timestamp) IS NULL OR t.created_at <= CAST(:endDate AS timestamp))
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:status AS varchar) IS NULL OR t.status = CAST(:status AS varchar))
            """, nativeQuery = true)
    long countByPriority(@Param("tenantId") UUID tenantId,
                         @Param("projectId") UUID projectId,
                         @Param("targetPriority") String targetPriority,
                         @Param("startDate") LocalDateTime startDate,
                         @Param("endDate") LocalDateTime endDate,
                         @Param("assigneeId") UUID assigneeId,
                         @Param("status") String status);

    @Query(value = """
            SELECT COUNT(*)
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND t.due_date IS NOT NULL
              AND t.due_date BETWEEN CAST(:dueFrom AS timestamp) AND CAST(:dueTo AS timestamp)
              AND (CAST(:startDate AS timestamp) IS NULL OR t.created_at >= CAST(:startDate AS timestamp))
              AND (CAST(:endDate AS timestamp) IS NULL OR t.created_at <= CAST(:endDate AS timestamp))
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:priority AS varchar) IS NULL OR t.priority = CAST(:priority AS varchar))
              AND (CAST(:status AS varchar) IS NULL OR t.status = CAST(:status AS varchar))
            """, nativeQuery = true)
    long countDueBetween(@Param("tenantId") UUID tenantId,
                         @Param("projectId") UUID projectId,
                         @Param("dueFrom") LocalDateTime dueFrom,
                         @Param("dueTo") LocalDateTime dueTo,
                         @Param("startDate") LocalDateTime startDate,
                         @Param("endDate") LocalDateTime endDate,
                         @Param("assigneeId") UUID assigneeId,
                         @Param("priority") String priority,
                         @Param("status") String status);

    @Query(value = """
            SELECT AVG(EXTRACT(EPOCH FROM (t.completed_at - t.created_at)) / 3600.0)
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND t.status = 'DONE'
              AND t.completed_at IS NOT NULL
              AND (CAST(:startDate AS timestamp) IS NULL OR t.created_at >= CAST(:startDate AS timestamp))
              AND (CAST(:endDate AS timestamp) IS NULL OR t.created_at <= CAST(:endDate AS timestamp))
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:priority AS varchar) IS NULL OR t.priority = CAST(:priority AS varchar))
            """, nativeQuery = true)
    Double averageCompletionTimeHours(@Param("tenantId") UUID tenantId,
                                      @Param("projectId") UUID projectId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      @Param("assigneeId") UUID assigneeId,
                                      @Param("priority") String priority);

    @Query(value = """
            SELECT AVG(EXTRACT(EPOCH FROM (CAST(:now AS timestamp) - t.created_at)) / 3600.0)
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND t.status NOT IN ('DONE', 'CANCELLED')
              AND (CAST(:startDate AS timestamp) IS NULL OR t.created_at >= CAST(:startDate AS timestamp))
              AND (CAST(:endDate AS timestamp) IS NULL OR t.created_at <= CAST(:endDate AS timestamp))
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:priority AS varchar) IS NULL OR t.priority = CAST(:priority AS varchar))
              AND (CAST(:status AS varchar) IS NULL OR t.status = CAST(:status AS varchar))
            """, nativeQuery = true)
    Double averageTaskAgeHours(@Param("tenantId") UUID tenantId,
                               @Param("projectId") UUID projectId,
                               @Param("now") LocalDateTime now,
                               @Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate,
                               @Param("assigneeId") UUID assigneeId,
                               @Param("priority") String priority,
                               @Param("status") String status);

    // ── Chart datasets ─────────────────────────────────────────────────

    @Query(value = """
            SELECT t.status, COUNT(*)
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND (CAST(:startDate AS timestamp) IS NULL OR t.created_at >= CAST(:startDate AS timestamp))
              AND (CAST(:endDate AS timestamp) IS NULL OR t.created_at <= CAST(:endDate AS timestamp))
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:priority AS varchar) IS NULL OR t.priority = CAST(:priority AS varchar))
            GROUP BY t.status
            ORDER BY t.status
            """, nativeQuery = true)
    List<Object[]> countGroupedByStatus(@Param("tenantId") UUID tenantId,
                                        @Param("projectId") UUID projectId,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        @Param("assigneeId") UUID assigneeId,
                                        @Param("priority") String priority);

    @Query(value = """
            SELECT t.priority, COUNT(*)
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND (CAST(:startDate AS timestamp) IS NULL OR t.created_at >= CAST(:startDate AS timestamp))
              AND (CAST(:endDate AS timestamp) IS NULL OR t.created_at <= CAST(:endDate AS timestamp))
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:status AS varchar) IS NULL OR t.status = CAST(:status AS varchar))
            GROUP BY t.priority
            ORDER BY t.priority
            """, nativeQuery = true)
    List<Object[]> countGroupedByPriority(@Param("tenantId") UUID tenantId,
                                          @Param("projectId") UUID projectId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          @Param("assigneeId") UUID assigneeId,
                                          @Param("status") String status);

    @Query(value = """
            SELECT TO_CHAR(t.created_at, 'Month'), EXTRACT(YEAR FROM t.created_at)::int, COUNT(*)
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND t.created_at BETWEEN CAST(:chartFrom AS timestamp) AND CAST(:chartTo AS timestamp)
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:priority AS varchar) IS NULL OR t.priority = CAST(:priority AS varchar))
              AND (CAST(:status AS varchar) IS NULL OR t.status = CAST(:status AS varchar))
            GROUP BY TO_CHAR(t.created_at, 'Month'), EXTRACT(YEAR FROM t.created_at),
                     TO_CHAR(t.created_at, 'YYYY-MM')
            ORDER BY TO_CHAR(t.created_at, 'YYYY-MM')
            """, nativeQuery = true)
    List<Object[]> countCreatedPerMonth(@Param("tenantId") UUID tenantId,
                                        @Param("projectId") UUID projectId,
                                        @Param("chartFrom") LocalDateTime chartFrom,
                                        @Param("chartTo") LocalDateTime chartTo,
                                        @Param("assigneeId") UUID assigneeId,
                                        @Param("priority") String priority,
                                        @Param("status") String status);

    @Query(value = """
            SELECT TO_CHAR(t.completed_at, 'Month'), EXTRACT(YEAR FROM t.completed_at)::int, COUNT(*)
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND t.status = 'DONE'
              AND t.completed_at IS NOT NULL
              AND t.completed_at BETWEEN CAST(:chartFrom AS timestamp) AND CAST(:chartTo AS timestamp)
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:priority AS varchar) IS NULL OR t.priority = CAST(:priority AS varchar))
            GROUP BY TO_CHAR(t.completed_at, 'Month'), EXTRACT(YEAR FROM t.completed_at),
                     TO_CHAR(t.completed_at, 'YYYY-MM')
            ORDER BY TO_CHAR(t.completed_at, 'YYYY-MM')
            """, nativeQuery = true)
    List<Object[]> countCompletedPerMonth(@Param("tenantId") UUID tenantId,
                                          @Param("projectId") UUID projectId,
                                          @Param("chartFrom") LocalDateTime chartFrom,
                                          @Param("chartTo") LocalDateTime chartTo,
                                          @Param("assigneeId") UUID assigneeId,
                                          @Param("priority") String priority);

    @Query(value = """
            SELECT ta.user_id, COUNT(*)
            FROM task_assignments ta
            JOIN tasks t ON t.id = ta.task_id
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND t.status NOT IN ('DONE', 'CANCELLED')
              AND (CAST(:startDate AS timestamp) IS NULL OR t.created_at >= CAST(:startDate AS timestamp))
              AND (CAST(:endDate AS timestamp) IS NULL OR t.created_at <= CAST(:endDate AS timestamp))
              AND (CAST(:priority AS varchar) IS NULL OR t.priority = CAST(:priority AS varchar))
              AND (CAST(:status AS varchar) IS NULL OR t.status = CAST(:status AS varchar))
            GROUP BY ta.user_id
            ORDER BY COUNT(*) DESC
            """, nativeQuery = true)
    List<Object[]> countAssigneeWorkload(@Param("tenantId") UUID tenantId,
                                         @Param("projectId") UUID projectId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         @Param("priority") String priority,
                                         @Param("status") String status);

    // ── Recent activity ────────────────────────────────────────────────

    @Query(value = """
            SELECT t.id, t.project_id, t.title, t.status, t.priority, t.type, t.created_at, t.completed_at
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND (CAST(:startDate AS timestamp) IS NULL OR t.created_at >= CAST(:startDate AS timestamp))
              AND (CAST(:endDate AS timestamp) IS NULL OR t.created_at <= CAST(:endDate AS timestamp))
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:priority AS varchar) IS NULL OR t.priority = CAST(:priority AS varchar))
              AND (CAST(:status AS varchar) IS NULL OR t.status = CAST(:status AS varchar))
            ORDER BY t.created_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findRecentlyCreated(@Param("tenantId") UUID tenantId,
                                       @Param("projectId") UUID projectId,
                                       @Param("limit") int limit,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate,
                                       @Param("assigneeId") UUID assigneeId,
                                       @Param("priority") String priority,
                                       @Param("status") String status);

    @Query(value = """
            SELECT t.id, t.project_id, t.title, t.status, t.priority, t.type, t.created_at, t.completed_at
            FROM tasks t
            WHERE t.tenant_id = :tenantId
              AND t.project_id = :projectId
              AND t.deleted_at IS NULL
              AND t.status = 'DONE'
              AND t.completed_at IS NOT NULL
              AND (CAST(:startDate AS timestamp) IS NULL OR t.created_at >= CAST(:startDate AS timestamp))
              AND (CAST(:endDate AS timestamp) IS NULL OR t.created_at <= CAST(:endDate AS timestamp))
              AND (CAST(:assigneeId AS uuid) IS NULL OR EXISTS (
                  SELECT 1 FROM task_assignments ta
                  WHERE ta.task_id = t.id AND ta.user_id = CAST(:assigneeId AS uuid)))
              AND (CAST(:priority AS varchar) IS NULL OR t.priority = CAST(:priority AS varchar))
            ORDER BY t.completed_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findRecentlyCompleted(@Param("tenantId") UUID tenantId,
                                         @Param("projectId") UUID projectId,
                                         @Param("limit") int limit,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         @Param("assigneeId") UUID assigneeId,
                                         @Param("priority") String priority);
}
