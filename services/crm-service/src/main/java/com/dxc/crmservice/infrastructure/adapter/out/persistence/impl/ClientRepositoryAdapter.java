package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.ClientRepository;
import com.dxc.crmservice.domain.exception.RecordNotFoundException;
import com.dxc.crmservice.domain.model.aggregate.Client;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.ClientEntity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.ClientRepositoryJpa;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper.ClientPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        ClientEntity clientEntity = clientPersistenceMapper.toEntity(client);
        ClientEntity savedClientEntity = clientRepositoryJpa.save(clientEntity);
        return clientPersistenceMapper.toDomain(savedClientEntity);
    }

    @Override
    public Client findById(UUID id, UUID tenantId) {
        return clientRepositoryJpa.findByIdAndTenantId(id, tenantId)
                .map(clientPersistenceMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Client update(Client client) {
        ClientEntity clientEntity = clientPersistenceMapper.toEntity(client);
        ClientEntity updatedClientEntity = clientRepositoryJpa.save(clientEntity);
        return clientPersistenceMapper.toDomain(updatedClientEntity);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        log.info("Deleting client with id: {} and tenantId: {}", id, tenantId);
        ClientEntity client = clientRepositoryJpa.findByIdAndTenantId(id, tenantId).orElseThrow( () -> new RecordNotFoundException("Client not found"));
        clientRepositoryJpa.delete(client);
    }

    @Override
    public Page<Client> findAll(UUID tenantId, Pageable pageable) {
        Page<ClientEntity> clientEntities = clientRepositoryJpa.findByTenantId(tenantId, pageable);
        return clientEntities.map(clientPersistenceMapper::toDomain);
    }}
