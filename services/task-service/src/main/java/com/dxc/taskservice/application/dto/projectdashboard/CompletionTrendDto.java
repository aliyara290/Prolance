package com.dxc.taskservice.application.dto.projectdashboard;

public record CompletionTrendDto(String month, int year, long created, long completed) {}
