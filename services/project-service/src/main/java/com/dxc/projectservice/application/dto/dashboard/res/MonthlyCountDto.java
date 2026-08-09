package com.dxc.projectservice.application.dto.dashboard.res;

/**
 * Represents a count grouped by month/year, used for bar/line chart time series.
 */
public record MonthlyCountDto(String month, int year, long count) {}
