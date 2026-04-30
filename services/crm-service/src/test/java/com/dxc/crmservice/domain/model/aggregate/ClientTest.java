package com.dxc.crmservice.domain.model.aggregate;

import com.dxc.crmservice.domain.exception.BusinessRuleViolationException;
import com.dxc.crmservice.domain.exception.ValidationException;
import com.dxc.crmservice.domain.model.valueobject.ClientStatus;
import com.dxc.crmservice.domain.model.valueobject.ClientType;
import com.dxc.crmservice.domain.model.valueobject.Source;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Client – Domain Aggregate")
class ClientTest {

    private static final UUID TENANT = UUID.randomUUID();

    private Client newClient() {
        return Client.create(TENANT, "Acme Corp", "Tech", "https://acme.com", "+33600000000", null,
                ClientType.B2B, Source.WEBSITE);
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("creates client with ACTIVE status and generated id")
        void happyPath() {
            Client client = newClient();

            assertThat(client.getId()).isNotNull();
            assertThat(client.getTenantId()).isEqualTo(TENANT);
            assertThat(client.getStatus()).isEqualTo(ClientStatus.ACTIVE);
            assertThat(client.getName()).isEqualTo("Acme Corp");
        }

        @Test
        @DisplayName("rejects blank name")
        void blankName() {
            assertThatThrownBy(() ->
                    Client.create(TENANT, "  ", "Tech", null, null, null, ClientType.B2B, Source.WEBSITE))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("rejects null tenantId")
        void nullTenantId() {
            assertThatThrownBy(() ->
                    Client.create(null, "Name", null, null, null, null, ClientType.B2B, Source.WEBSITE))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    @DisplayName("deactivate() sets status to INACTIVE")
    void deactivate() {
        Client client = newClient();
        client.deactivate();

        assertThat(client.getStatus()).isEqualTo(ClientStatus.INACTIVE);
    }

    @Test
    @DisplayName("activate() restores ACTIVE status")
    void activate() {
        Client client = newClient();
        client.deactivate();
        client.activate();

        assertThat(client.getStatus()).isEqualTo(ClientStatus.ACTIVE);
    }

    @Test
    @DisplayName("markAsArchived() sets status to ARCHIVED")
    void archive() {
        Client client = newClient();
        client.markAsArchived();

        assertThat(client.getStatus()).isEqualTo(ClientStatus.ARCHIVED);
    }

    @Test
    @DisplayName("markAsArchived() twice throws BusinessRuleViolationException")
    void archiveTwice() {
        Client client = newClient();
        client.markAsArchived();

        assertThatThrownBy(client::markAsArchived)
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    @DisplayName("updateProfile() on archived client throws BusinessRuleViolationException")
    void cannotUpdateArchived() {
        Client client = newClient();
        client.markAsArchived();

        assertThatThrownBy(() ->
                client.updateProfile("New Name", null, null, null, null))
                .isInstanceOf(BusinessRuleViolationException.class);
    }
}
