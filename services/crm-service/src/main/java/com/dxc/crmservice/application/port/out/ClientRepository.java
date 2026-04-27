package com.dxc.crmservice.application.port.out;

import com.dxc.crmservice.domain.model.aggregate.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;


public interface ClientRepository {
    Client save(Client client);
    Client findById(UUID id,UUID tenantId);
    Client update(Client client);
    void delete(UUID id, UUID tenantId);
    Page<Client> findAll(UUID tenantId, Pageable pageable);
}