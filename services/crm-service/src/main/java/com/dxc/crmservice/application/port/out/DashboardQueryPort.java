package com.dxc.crmservice.application.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DashboardQueryPort {

    // KPIs
    long countLeads(UUID tenantId, LocalDateTime from, LocalDateTime to);

    long countQualifiedLeads(UUID tenantId, LocalDateTime from, LocalDateTime to);

    long countOpenDeals(UUID tenantId, LocalDateTime from, LocalDateTime to);

    long countWonDeals(UUID tenantId, LocalDateTime from, LocalDateTime to);

    Double sumPipelineValue(UUID tenantId, LocalDateTime from, LocalDateTime to);

    long countClients(UUID tenantId, LocalDateTime from, LocalDateTime to);

    long countNewClientsThisMonth(UUID tenantId, LocalDateTime monthStart, LocalDateTime monthEnd);

    long countContacts(UUID tenantId, LocalDateTime from, LocalDateTime to);

    List<Object[]> getRecentActivities(UUID tenantId, int limit);

    // Charts
    List<Object[]> countLeadsGroupedByStatus(UUID tenantId, LocalDateTime from, LocalDateTime to);

    List<Object[]> countLeadsGroupedByMonth(UUID tenantId, LocalDateTime from, LocalDateTime to);

    List<Object[]> countDealsGroupedByStage(UUID tenantId, LocalDateTime from, LocalDateTime to);

    List<Object[]> countWonVsLostDeals(UUID tenantId, LocalDateTime from, LocalDateTime to);

    List<Object[]> sumPipelineValueGroupedByStage(UUID tenantId, LocalDateTime from, LocalDateTime to);

    List<Object[]> countClientsGroupedByMonth(UUID tenantId, LocalDateTime from, LocalDateTime to);
}
