package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.DashboardQueryPort;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.DashboardQueryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DashboardQueryAdapter implements DashboardQueryPort {

    private final DashboardQueryJpa jpa;

    @Override
    public long countLeads(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.countLeads(tenantId, from, to);
    }

    @Override
    public long countQualifiedLeads(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.countQualifiedLeads(tenantId, from, to);
    }

    @Override
    public long countOpenDeals(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.countOpenDeals(tenantId, from, to);
    }

    @Override
    public long countWonDeals(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.countWonDeals(tenantId, from, to);
    }

    @Override
    public Double sumPipelineValue(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.sumPipelineValue(tenantId, from, to);
    }

    @Override
    public long countClients(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.countClients(tenantId, from, to);
    }

    @Override
    public long countNewClientsThisMonth(UUID tenantId, LocalDateTime monthStart, LocalDateTime monthEnd) {
        return jpa.countNewClientsThisMonth(tenantId, monthStart, monthEnd);
    }

    @Override
    public long countContacts(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.countContacts(tenantId, from, to);
    }

    @Override
    public List<Object[]> getRecentActivities(UUID tenantId, int limit) {
        return jpa.findRecentActivities(tenantId, org.springframework.data.domain.PageRequest.of(0, limit));
    }

    @Override
    public List<Object[]> countLeadsGroupedByStatus(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.countLeadsGroupedByStatus(tenantId, from, to);
    }

    @Override
    public List<Object[]> countLeadsGroupedByMonth(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.countLeadsGroupedByMonth(tenantId, from, to);
    }

    @Override
    public List<Object[]> countDealsGroupedByStage(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.countDealsGroupedByStage(tenantId, from, to);
    }

    @Override
    public List<Object[]> countWonVsLostDeals(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.countWonVsLostDeals(tenantId, from, to);
    }

    @Override
    public List<Object[]> sumPipelineValueGroupedByStage(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.sumPipelineValueGroupedByStage(tenantId, from, to);
    }

    @Override
    public List<Object[]> countClientsGroupedByMonth(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        return jpa.countClientsGroupedByMonth(tenantId, from, to);
    }
}
