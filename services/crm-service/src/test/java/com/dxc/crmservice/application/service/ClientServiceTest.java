package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.dto.client.req.CreateClientRequest;
import com.dxc.crmservice.application.dto.client.req.UpdateClientRequest;
import com.dxc.crmservice.application.dto.client.res.ClientResponse;
import com.dxc.crmservice.application.mapper.ClientMapper;
import com.dxc.crmservice.application.port.out.ClientRepository;
import com.dxc.crmservice.domain.exception.ServiceLogicException;
import com.dxc.crmservice.domain.model.aggregate.Client;
import com.dxc.crmservice.domain.model.valueobject.ClientStatus;
import com.dxc.crmservice.domain.model.valueobject.ClientType;
import com.dxc.crmservice.domain.model.valueobject.Source;
import com.dxc.crmservice.infrastructure.config.TenantContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService – Application Layer")
class ClientServiceTest {

    @Mock ClientRepository clientRepository;
    @Mock ClientMapper      clientMapper;

    @InjectMocks ClientService clientService;

    private static final UUID TENANT_ID = UUID.randomUUID();
    private static final UUID CLIENT_ID = UUID.randomUUID();

    @BeforeEach
    void setTenant() {
        TenantContextHolder.setTenantId(TENANT_ID.toString());
    }

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
    }

    // helpers

    private Client stubClient() {
        return Client.create(TENANT_ID, "Acme Corp", "Tech",
                "https://acme.com", "+33600000000", null, ClientType.B2B, Source.WEBSITE);
    }

    private ClientResponse stubResponse(Client client) {
        return new ClientResponse(client.getId(), client.getName(), client.getIndustry(),
                client.getWebsite(), client.getPhone(), null,
                null, // country
                client.getStatus(), client.getType(), client.getSource(),
                client.getCreatedAt(), client.getUpdatedAt());
    }

    private CreateClientRequest createRequest() {
        return new CreateClientRequest("Acme Corp", "Tech", "https://acme.com",
                "+33600000000", null, ClientType.B2B, Source.WEBSITE);
    }

    private UpdateClientRequest updateRequest(String name) {
        return new UpdateClientRequest(name, null, null, null, null, null, null, null);
    }

    @Nested
    @DisplayName("createClient()")
    class CreateClient {

        @Test
        @DisplayName("saves client and returns response")
        void success() {
            Client client   = stubClient();
            ClientResponse response = stubResponse(client);

            when(clientMapper.toDomain(any(), eq(TENANT_ID))).thenReturn(client);
            when(clientRepository.save(client)).thenReturn(client);
            when(clientMapper.toResponse(client)).thenReturn(response);

            ClientResponse result = clientService.createClient(createRequest());

            assertThat(result).isEqualTo(response);
            verify(clientRepository).save(client);
        }
    }

    @Nested
    @DisplayName("updateClient()")
    class UpdateClient {

        @Test
        @DisplayName("updates name and returns response")
        void success() {
            Client client = stubClient();
            ClientResponse response = stubResponse(client);

            when(clientRepository.findById(CLIENT_ID, TENANT_ID)).thenReturn(client);
            when(clientRepository.update(client)).thenReturn(client);
            when(clientMapper.toResponse(client)).thenReturn(response);

            UpdateClientRequest req = updateRequest("New Name");
            ClientResponse result = clientService.updateClient(CLIENT_ID, req);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws ServiceLogicException when client not found")
        void notFound() {
            when(clientRepository.findById(CLIENT_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() ->
                    clientService.updateClient(CLIENT_ID, updateRequest("n")))
                    .isInstanceOf(ServiceLogicException.class);
        }
    }

    @Nested
    @DisplayName("getClient()")
    class GetClient {

        @Test
        @DisplayName("returns client response for existing id")
        void success() {
            Client client = stubClient();
            ClientResponse response = stubResponse(client);

            when(clientRepository.findById(CLIENT_ID, TENANT_ID)).thenReturn(client);
            when(clientMapper.toResponse(client)).thenReturn(response);

            ClientResponse result = clientService.getClient(CLIENT_ID);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws ServiceLogicException when not found")
        void notFound() {
            when(clientRepository.findById(CLIENT_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() -> clientService.getClient(CLIENT_ID))
                    .isInstanceOf(ServiceLogicException.class);
        }
    }

    @Test
    @DisplayName("deleteClient() delegates to repository")
    void deleteClient() {
        clientService.deleteClient(CLIENT_ID);

        verify(clientRepository).delete(CLIENT_ID, TENANT_ID);
    }

    @Test
    @DisplayName("getAllClients() maps page correctly")
    void getAllClients() {
        Client client = stubClient();
        ClientResponse response = stubResponse(client);
        Page<Client> page = new PageImpl<>(List.of(client));

        when(clientRepository.findAll(TENANT_ID, PageRequest.of(0, 10))).thenReturn(page);
        when(clientMapper.toResponse(client)).thenReturn(response);

        Page<ClientResponse> result = clientService.getAllClients(PageRequest.of(0, 10));

        assertThat(result.getContent()).containsExactly(response);
    }
}
