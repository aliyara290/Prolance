package com.dxc.projectservice.infrastructure.adapter.in.rest.controller;

import com.dxc.projectservice.application.dto.validation.CanCreateTaskInProjectReq;
import com.dxc.projectservice.application.dto.validation.CanCreateTaskInProjectRes;
import com.dxc.projectservice.application.port.in.validation.ValidationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}")
@RequiredArgsConstructor
public class ValidationController {

    private final ValidationUseCase validationUseCase;

    @PostMapping("/task-creation-validation")
    public ResponseEntity<CanCreateTaskInProjectRes> canCreateTaskInProject(
            @PathVariable("projectId") UUID projectId,
            @RequestBody CanCreateTaskInProjectReq req
    ) {
        CanCreateTaskInProjectRes response = validationUseCase.canCreateTaskInProject(projectId, req);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
