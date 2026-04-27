package com.dxc.crmservice.application.port.out;

import com.dxc.crmservice.domain.model.aggregate.Client;

import java.util.List;
import java.util.UUID;


public interface ClientRepository {
    Client save(Client client);
    Client findById(UUID id);
    Client update(Client client);
    void delete(UUID id, UUID tenantId);
    List<Client> findAll(UUID tenantId);
}