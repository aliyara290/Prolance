package com.dxc.crmservice.application.port.in;

import com.dxc.crmservice.application.dto.dashboard.DashboardResponse;

import java.time.LocalDate;

public interface DashboardUseCase {
    DashboardResponse getDashboard(LocalDate from, LocalDate to);
}
