package com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DashboardQueryJpa extends JpaRepository<LeadEntity, UUID> {

    // ── Lead KPIs ──

    @Query("SELECT COUNT(l) FROM LeadEntity l WHERE l.tenantId = :tenantId " +
            "AND l.createdAt >= :from AND l.createdAt <= :to")
    long countLeads(@Param("tenantId") UUID tenantId,
                    @Param("from") LocalDateTime from,
                    @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(l) FROM LeadEntity l WHERE l.tenantId = :tenantId " +
            "AND l.status = 'QUALIFIED' AND l.createdAt >= :from AND l.createdAt <= :to")
    long countQualifiedLeads(@Param("tenantId") UUID tenantId,
                             @Param("from") LocalDateTime from,
                             @Param("to") LocalDateTime to);

    // ── Opportunity KPIs ──

    @Query("SELECT COUNT(o) FROM OpportunityEntity o WHERE o.tenantId = :tenantId " +
            "AND o.stage NOT IN (com.dxc.crmservice.domain.model.valueobject.Stage.WON, com.dxc.crmservice.domain.model.valueobject.Stage.LOST) " +
            "AND o.createdAt >= :from AND o.createdAt <= :to")
    long countOpenDeals(@Param("tenantId") UUID tenantId,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(o) FROM OpportunityEntity o WHERE o.tenantId = :tenantId " +
            "AND o.stage = com.dxc.crmservice.domain.model.valueobject.Stage.WON " +
            "AND o.createdAt >= :from AND o.createdAt <= :to")
    long countWonDeals(@Param("tenantId") UUID tenantId,
                       @Param("from") LocalDateTime from,
                       @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(o.expectedRevenue), 0) FROM OpportunityEntity o WHERE o.tenantId = :tenantId " +
            "AND o.stage NOT IN (com.dxc.crmservice.domain.model.valueobject.Stage.WON, com.dxc.crmservice.domain.model.valueobject.Stage.LOST) " +
            "AND o.createdAt >= :from AND o.createdAt <= :to")
    Double sumPipelineValue(@Param("tenantId") UUID tenantId,
                            @Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to);

    // ── Client KPIs ──

    @Query("SELECT COUNT(c) FROM ClientEntity c WHERE c.tenantId = :tenantId " +
            "AND c.createdAt >= :from AND c.createdAt <= :to")
    long countClients(@Param("tenantId") UUID tenantId,
                      @Param("from") LocalDateTime from,
                      @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(c) FROM ClientEntity c WHERE c.tenantId = :tenantId " +
            "AND c.createdAt >= :monthStart AND c.createdAt <= :monthEnd")
    long countNewClientsThisMonth(@Param("tenantId") UUID tenantId,
                                  @Param("monthStart") LocalDateTime monthStart,
                                  @Param("monthEnd") LocalDateTime monthEnd);

    // ── Contact KPIs ──

    @Query("SELECT COUNT(ct) FROM ContactEntity ct WHERE ct.tenantId = :tenantId " +
            "AND ct.createdAt >= :from AND ct.createdAt <= :to")
    long countContacts(@Param("tenantId") UUID tenantId,
                       @Param("from") LocalDateTime from,
                       @Param("to") LocalDateTime to);

    // ── Lead Charts ──

    @Query("SELECT l.status, COUNT(l) FROM LeadEntity l WHERE l.tenantId = :tenantId " +
            "AND l.createdAt >= :from AND l.createdAt <= :to GROUP BY l.status")
    List<Object[]> countLeadsGroupedByStatus(@Param("tenantId") UUID tenantId,
                                             @Param("from") LocalDateTime from,
                                             @Param("to") LocalDateTime to);

    @Query("SELECT YEAR(l.createdAt), MONTH(l.createdAt), COUNT(l) FROM LeadEntity l " +
            "WHERE l.tenantId = :tenantId AND l.createdAt >= :from AND l.createdAt <= :to " +
            "GROUP BY YEAR(l.createdAt), MONTH(l.createdAt) " +
            "ORDER BY YEAR(l.createdAt), MONTH(l.createdAt)")
    List<Object[]> countLeadsGroupedByMonth(@Param("tenantId") UUID tenantId,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to);

    // ── Opportunity Charts ──

    @Query("SELECT o.stage, COUNT(o) FROM OpportunityEntity o WHERE o.tenantId = :tenantId " +
            "AND o.createdAt >= :from AND o.createdAt <= :to GROUP BY o.stage")
    List<Object[]> countDealsGroupedByStage(@Param("tenantId") UUID tenantId,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to);

    @Query("SELECT o.stage, COUNT(o) FROM OpportunityEntity o WHERE o.tenantId = :tenantId " +
            "AND o.stage IN (com.dxc.crmservice.domain.model.valueobject.Stage.WON, com.dxc.crmservice.domain.model.valueobject.Stage.LOST) " +
            "AND o.createdAt >= :from AND o.createdAt <= :to GROUP BY o.stage")
    List<Object[]> countWonVsLostDeals(@Param("tenantId") UUID tenantId,
                                       @Param("from") LocalDateTime from,
                                       @Param("to") LocalDateTime to);

    @Query("SELECT o.stage, COALESCE(SUM(o.expectedRevenue), 0) FROM OpportunityEntity o " +
            "WHERE o.tenantId = :tenantId " +
            "AND o.stage NOT IN (com.dxc.crmservice.domain.model.valueobject.Stage.WON, com.dxc.crmservice.domain.model.valueobject.Stage.LOST) " +
            "AND o.createdAt >= :from AND o.createdAt <= :to GROUP BY o.stage")
    List<Object[]> sumPipelineValueGroupedByStage(@Param("tenantId") UUID tenantId,
                                                   @Param("from") LocalDateTime from,
                                                   @Param("to") LocalDateTime to);

    // ── Client Charts ──

    @Query("SELECT YEAR(c.createdAt), MONTH(c.createdAt), COUNT(c) FROM ClientEntity c " +
            "WHERE c.tenantId = :tenantId AND c.createdAt >= :from AND c.createdAt <= :to " +
            "GROUP BY YEAR(c.createdAt), MONTH(c.createdAt) " +
            "ORDER BY YEAR(c.createdAt), MONTH(c.createdAt)")
    List<Object[]> countClientsGroupedByMonth(@Param("tenantId") UUID tenantId,
                                              @Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);
}
