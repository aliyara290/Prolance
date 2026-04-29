package com.dxc.crmservice.infrastructure.adapter.in.rest.controller;

import com.dxc.crmservice.application.dto.client.req.CreateClientRequest;
import com.dxc.crmservice.application.dto.client.req.UpdateClientRequest;
import com.dxc.crmservice.application.dto.client.res.ClientResponse;
import com.dxc.crmservice.application.port.in.ClientUseCase;
import com.dxc.crmservice.infrastructure.adapter.in.rest.response.ApiResponse;
import com.dxc.crmservice.infrastructure.adapter.in.rest.response.PageMeta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientUseCase clientUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponse>> createClient(@Valid @RequestBody CreateClientRequest request) {
        ClientResponse clientResponse = clientUseCase.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(clientResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> updateClient(@PathVariable("id") UUID id, @Valid @RequestBody UpdateClientRequest request) {
        ClientResponse clientResponse = clientUseCase.updateClient(id, request);
        return ResponseEntity.ok(ApiResponse.success(clientResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> getClient(@PathVariable("id") UUID id) {
        ClientResponse clientResponse = clientUseCase.getClient(id);
        return ResponseEntity.ok(ApiResponse.success(clientResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getAllClients(Pageable pageable) {
        Page<ClientResponse> clients = clientUseCase.getAllClients(pageable);
        PageMeta meta = PageMeta.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .hasNext(clients.hasNext())
                .hasPrevious(clients.hasPrevious())
                .totalPages(clients.getTotalPages())
                .totalElements(clients.getTotalElements())
                .build();

        return ResponseEntity.ok(ApiResponse.success(clients.getContent(), meta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable("id") UUID id) {
        clientUseCase.deleteClient(id);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }
}
