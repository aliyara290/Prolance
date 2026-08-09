package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.dto.dashboard.ChartEntry;
import com.dxc.crmservice.application.dto.dashboard.DashboardResponse;
import com.dxc.crmservice.application.dto.dashboard.DashboardResponse.ChartData;
import com.dxc.crmservice.application.dto.dashboard.DashboardResponse.KpiData;
import com.dxc.crmservice.application.dto.dashboard.TimeSeriesEntry;
import com.dxc.crmservice.application.port.in.DashboardUseCase;
import com.dxc.crmservice.application.port.out.DashboardQueryPort;
import com.dxc.crmservice.application.security.TenantGuard;
import com.dxc.crmservice.application.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService implements DashboardUseCase {

    private final TenantGuard tenantGuard;
    private final DashboardQueryPort queryPort;

    @Override
    public DashboardResponse getDashboard(LocalDate from, LocalDate to) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);

        LocalDateTime fromDt = from != null ? from.atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime toDt = to != null ? to.atTime(LocalTime.MAX) : LocalDateTime.of(2099, 12, 31, 23, 59, 59);

        KpiData kpis = buildKpis(tenantId, fromDt, toDt);
        ChartData charts = buildCharts(tenantId, fromDt, toDt);

        return new DashboardResponse(kpis, charts);
    }

    private KpiData buildKpis(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        long totalLeads = queryPort.countLeads(tenantId, from, to);
        long qualifiedLeads = queryPort.countQualifiedLeads(tenantId, from, to);
        double conversionRate = totalLeads > 0 ? (qualifiedLeads * 100.0) / totalLeads : 0.0;

        long openDeals = queryPort.countOpenDeals(tenantId, from, to);
        long wonDeals = queryPort.countWonDeals(tenantId, from, to);

        Double pipelineValue = queryPort.sumPipelineValue(tenantId, from, to);
        double totalPipelineValue = pipelineValue != null ? pipelineValue : 0.0;

        long totalClients = queryPort.countClients(tenantId, from, to);

        YearMonth currentMonth = YearMonth.now();
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);
        long newClientsThisMonth = queryPort.countNewClientsThisMonth(tenantId, monthStart, monthEnd);

        long totalContacts = queryPort.countContacts(tenantId, from, to);

        List<com.dxc.crmservice.application.dto.dashboard.RecentActivityDto> recentActivities = queryPort.getRecentActivities(tenantId, 5)
                .stream()
                .map(row -> new com.dxc.crmservice.application.dto.dashboard.RecentActivityDto(
                        (UUID) row[0],
                        row[1].toString(), // action
                        (String) row[2], // message
                        row[3].toString(), // entityType
                        (LocalDateTime) row[4] // createdAt
                ))
                .toList();

        return new KpiData(
                totalLeads,
                Math.round(conversionRate * 100.0) / 100.0,
                openDeals,
                wonDeals,
                totalPipelineValue,
                totalClients,
                newClientsThisMonth,
                totalContacts,
                recentActivities
        );
    }

    private ChartData buildCharts(UUID tenantId, LocalDateTime from, LocalDateTime to) {
        List<ChartEntry> leadsByStatus = mapToChartEntries(queryPort.countLeadsGroupedByStatus(tenantId, from, to));
        List<TimeSeriesEntry> leadsByMonth = mapToTimeSeries(queryPort.countLeadsGroupedByMonth(tenantId, from, to));
        List<ChartEntry> dealsByStage = mapToChartEntries(queryPort.countDealsGroupedByStage(tenantId, from, to));
        List<ChartEntry> wonVsLost = mapToChartEntries(queryPort.countWonVsLostDeals(tenantId, from, to));
        List<ChartEntry> pipelineByStage = mapToChartEntries(queryPort.sumPipelineValueGroupedByStage(tenantId, from, to));
        List<TimeSeriesEntry> clientsByMonth = mapToTimeSeries(queryPort.countClientsGroupedByMonth(tenantId, from, to));

        return new ChartData(leadsByStatus, leadsByMonth, dealsByStage, wonVsLost, pipelineByStage, clientsByMonth);
    }

    private List<ChartEntry> mapToChartEntries(List<Object[]> rows) {
        return rows.stream()
                .map(row -> new ChartEntry(
                        row[0].toString(),
                        ((Number) row[1]).doubleValue()
                ))
                .toList();
    }

    private List<TimeSeriesEntry> mapToTimeSeries(List<Object[]> rows) {
        return rows.stream()
                .map(row -> new TimeSeriesEntry(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).longValue()
                ))
                .toList();
    }
}
