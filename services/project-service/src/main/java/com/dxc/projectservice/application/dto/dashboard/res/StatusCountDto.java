package com.dxc.projectservice.application.dto.dashboard.res;

/**
 * Represents a count grouped by status, used for pie/donut charts.
 */
public record StatusCountDto(String status, long count) {}
