package com.dxc.billingservice.application.port.in;

import com.dxc.billingservice.application.dto.req.LogTimeRequest;
import com.dxc.billingservice.application.dto.res.TimeEntryResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TimeTrackingUseCase {
    TimeEntryResponse logTime(LogTimeRequest request);
    
    TimeEntryResponse updateTimeEntry(UUID timeEntryId, LogTimeRequest request);
    
    void deleteTimeEntry(UUID timeEntryId);
    
    List<TimeEntryResponse> getProjectTimeEntries(UUID projectId, LocalDateTime start, LocalDateTime end);
}
