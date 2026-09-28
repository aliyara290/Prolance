package com.dxc.billingservice.infrastructure.adapter.in.rest.controller;

import com.dxc.billingservice.application.dto.req.LogTimeRequest;
import com.dxc.billingservice.application.dto.res.TimeEntryResponse;
import com.dxc.billingservice.application.port.in.TimeTrackingUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/time-entries")
@RequiredArgsConstructor
public class TimeTrackingController {

    private final TimeTrackingUseCase timeTrackingUseCase;

    @PostMapping
    public ResponseEntity<TimeEntryResponse> logTime(@Valid @RequestBody LogTimeRequest request) {
        TimeEntryResponse response = timeTrackingUseCase.logTime(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeEntryResponse> updateTimeEntry(
            @PathVariable("id") UUID id,
            @Valid @RequestBody LogTimeRequest request) {
        TimeEntryResponse response = timeTrackingUseCase.updateTimeEntry(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeEntry(@PathVariable("id") UUID id) {
        timeTrackingUseCase.deleteTimeEntry(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TimeEntryResponse>> getProjectTimeEntries(
            @PathVariable("projectId") UUID projectId,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        List<TimeEntryResponse> responses = timeTrackingUseCase.getProjectTimeEntries(projectId, start, end);
        return ResponseEntity.ok(responses);
    }
}
