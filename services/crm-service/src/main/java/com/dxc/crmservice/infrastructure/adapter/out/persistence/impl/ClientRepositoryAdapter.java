package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.ClientRepository;
import com.dxc.crmservice.domain.model.aggregate.Client;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.ClientEntity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.ClientRepositoryJpa;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper.ClientPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientRepositoryAdapter implements ClientRepository {

    private final ClientRepositoryJpa clientRepositoryJpa;
    private final ClientPersistenceMapper clientPersistenceMapper;

    @Override
    public Client save(Client client) {
        ClientEntity entity = clientPersistenceMapper.toEntity(client);
        return clientPersistenceMapper.toDomain(clientRepositoryJpa.save(entity));
    }

    @Override
    public Client findById(UUID id) {
        return clientRepositoryJpa.findById(id)
                .map(clientPersistenceMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Client update(Client client) {
        return save(client);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        clientRepositoryJpa.findByIdAndTenantId(id, tenantId).ifPresent(clientRepositoryJpa::delete);
    }

    @Override
    public List<Client> findAll(UUID tenantId) {
        return clientRepositoryJpa.findByTenantId(tenantId).stream()
                .map(clientPersistenceMapper::toDomain)
                .toList();
    }
}
