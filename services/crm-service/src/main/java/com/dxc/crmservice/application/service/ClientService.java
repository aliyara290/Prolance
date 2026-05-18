package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.dto.client.req.CreateClientRequest;
import com.dxc.crmservice.application.dto.client.req.UpdateClientRequest;
import com.dxc.crmservice.application.dto.client.res.ClientResponse;
import com.dxc.crmservice.application.mapper.ClientMapper;
import com.dxc.crmservice.application.port.in.ClientUseCase;
import com.dxc.crmservice.application.port.out.ClientRepository;
import com.dxc.crmservice.application.security.TenantGuard;
import com.dxc.crmservice.application.utils.Utils;
import com.dxc.crmservice.domain.exception.ServiceLogicException;
import com.dxc.crmservice.domain.model.aggregate.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ClientService implements ClientUseCase {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final TenantGuard tenantGuard;

    @Override
    public ClientResponse createClient(CreateClientRequest request) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        try {
            Client client = clientMapper.toDomain(request, tenantId);
            Client savedClient = clientRepository.save(client);
            return clientMapper.toResponse(savedClient);
        } catch (Exception e) {
            log.error("Error creating client: " + e.getMessage());
            throw new ServiceLogicException("Failed to create client");
        }
    }

    @Override
    public ClientResponse updateClient(UUID id, UpdateClientRequest request) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        try {
            Client client = clientRepository.findById(id, tenantId);
            if (client == null) {
                throw new ServiceLogicException("Client not found");
            }

            client.updateProfile(
                    request.name() != null ? request.name() : client.getName(),
                    request.industry() != null ? request.industry() : client.getIndustry(),
                    request.website() != null ? request.website() : client.getWebsite(),
                    request.phone() != null ? request.phone() : client.getPhone(),
                    request.address() != null ? clientMapper.toAddress(request.address()) : client.getAddress(),
                    request.annualRevenue() != null ? request.annualRevenue() : client.getAnnualRevenue(),
                    request.fax() != null ? request.fax() : client.getFax(),
                    request.ownership() != null ? request.ownership() : client.getOwnership(),
                    request.sicCode() != null ? request.sicCode() : client.getSicCode(),
                    request.description() != null ? request.description() : client.getDescription()
            );

            Client updatedClient = clientRepository.update(client);
            return clientMapper.toResponse(updatedClient);
        } catch (Exception e) {
            log.error("Error updating client: " + e.getMessage());
            throw new ServiceLogicException("Failed to update client: " + e.getMessage());
        }
    }

    @Override
    public void deleteClient(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        try {
            clientRepository.delete(id, tenantId);
        } catch (Exception e) {
            log.error("Error deleting client: " + e.getMessage());
            throw new ServiceLogicException("Failed to delete client");
        }
    }

    @Override
    public ClientResponse getClient(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        Client client = clientRepository.findById(id, tenantId);
        if (client == null) {
            throw new ServiceLogicException("Client not found");
        }
        return clientMapper.toResponse(client);
    }

    @Override
    public Page<ClientResponse> getAllClients(Pageable pageable) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        Page<Client> clients = clientRepository.findAll(tenantId, pageable);
        return clients.map(clientMapper::toResponse);
    }
}
