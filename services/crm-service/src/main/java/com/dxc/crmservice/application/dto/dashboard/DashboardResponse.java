package com.dxc.crmservice.application.dto.dashboard;

import java.util.List;

public record DashboardResponse(KpiData kpis, ChartData charts) {

    public record KpiData(
            long totalLeads,
            double leadConversionRate,
            long openDeals,
            long wonDeals,
            double totalPipelineValue,
            long totalClients,
            long newClientsThisMonth,
            long totalContacts
    ) {}

    public record ChartData(
            List<ChartEntry> leadsByStatus,
            List<TimeSeriesEntry> leadsByMonth,
            List<ChartEntry> dealsByStage,
            List<ChartEntry> wonVsLost,
            List<ChartEntry> pipelineValueByStage,
            List<TimeSeriesEntry> clientsByMonth
    ) {}
}
