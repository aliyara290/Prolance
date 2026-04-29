package com.dxc.crmservice.application.port.in;

import com.dxc.crmservice.application.dto.client.req.CreateClientRequest;
import com.dxc.crmservice.application.dto.client.req.UpdateClientRequest;
import com.dxc.crmservice.application.dto.client.res.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ClientUseCase {
    ClientResponse createClient(CreateClientRequest request);
    ClientResponse updateClient(UUID id, UpdateClientRequest request);
    void deleteClient(UUID id);
    ClientResponse getClient(UUID id);
    Page<ClientResponse> getAllClients(Pageable pageable);
}
