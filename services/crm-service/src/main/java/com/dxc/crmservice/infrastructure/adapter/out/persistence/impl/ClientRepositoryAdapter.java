package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.ClientRepository;
import com.dxc.crmservice.domain.model.aggregate.Client;

import java.util.List;
import java.util.UUID;

public class ClientRepositoryAdapter implements ClientRepository {

    @Override
    public Client save(Client client) {
        return null;
    }

    @Override
    public Client findById(UUID id) {
        return null;
    }

    @Override
    public Client update(Client client) {
        return null;
    }

    @Override
    public void delete(UUID id, UUID tenantId) {

    }

    @Override
    public List<Client> findAll(UUID tenantId) {
        return List.of();
    }
}
