package com.dxc.crmservice.domain.model.entity;

import com.dxc.crmservice.domain.exception.ValidationException;
import com.dxc.crmservice.domain.model.valueobject.InfluenceLevel;
import com.dxc.crmservice.domain.model.valueobject.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Contact – Domain Entity")
class ContactTest {

    private static final UUID TENANT = UUID.randomUUID();
    private static final UUID CLIENT = UUID.randomUUID();

    private Contact newContact() {
        return Contact.create(TENANT, CLIENT, "John", "Doe",
                "john@acme.com", "+33600000000", Role.CEO, InfluenceLevel.HIGH,
                false, "Notes");
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("creates contact with generated id")
        void happyPath() {
            Contact contact = newContact();

            assertThat(contact.getId()).isNotNull();
            assertThat(contact.getTenantId()).isEqualTo(TENANT);
            assertThat(contact.getClientId()).isEqualTo(CLIENT);
            assertThat(contact.getFirstName()).isEqualTo("John");
            assertThat(contact.getLastName()).isEqualTo("Doe");
            assertThat(contact.getEmail()).isEqualTo("john@acme.com");
            assertThat(contact.isPrimary()).isFalse();
        }

        @Test
        @DisplayName("rejects blank first name")
        void blankFirstName() {
            assertThatThrownBy(() ->
                    Contact.create(TENANT, CLIENT, "  ", "Doe",
                            "john@acme.com", null, Role.CEO, InfluenceLevel.HIGH, false, null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("rejects null first name")
        void nullFirstName() {
            assertThatThrownBy(() ->
                    Contact.create(TENANT, CLIENT, null, "Doe",
                            "john@acme.com", null, Role.CEO, InfluenceLevel.HIGH, false, null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("rejects blank last name")
        void blankLastName() {
            assertThatThrownBy(() ->
                    Contact.create(TENANT, CLIENT, "John", "  ",
                            "john@acme.com", null, Role.CEO, InfluenceLevel.HIGH, false, null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("rejects invalid email")
        void invalidEmail() {
            assertThatThrownBy(() ->
                    Contact.create(TENANT, CLIENT, "John", "Doe",
                            "not-an-email", null, Role.CEO, InfluenceLevel.HIGH, false, null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("rejects null email")
        void nullEmail() {
            assertThatThrownBy(() ->
                    Contact.create(TENANT, CLIENT, "John", "Doe",
                            null, null, Role.CEO, InfluenceLevel.HIGH, false, null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("rejects null tenantId")
        void nullTenantId() {
            assertThatThrownBy(() ->
                    Contact.create(null, CLIENT, "John", "Doe",
                            "john@acme.com", null, Role.CEO, InfluenceLevel.HIGH, false, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("rejects null clientId")
        void nullClientId() {
            assertThatThrownBy(() ->
                    Contact.create(TENANT, null, "John", "Doe",
                            "john@acme.com", null, Role.CEO, InfluenceLevel.HIGH, false, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("rejects null role")
        void nullRole() {
            assertThatThrownBy(() ->
                    Contact.create(TENANT, CLIENT, "John", "Doe",
                            "john@acme.com", null, null, InfluenceLevel.HIGH, false, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    @DisplayName("markAsPrimary() sets primary flag")
    void markAsPrimary() {
        Contact contact = newContact();
        assertThat(contact.isPrimary()).isFalse();

        contact.markAsPrimary();
        assertThat(contact.isPrimary()).isTrue();
    }

    @Test
    @DisplayName("demotePrimary() clears primary flag")
    void demotePrimary() {
        Contact contact = Contact.create(TENANT, CLIENT, "John", "Doe",
                "john@acme.com", null, Role.CEO, InfluenceLevel.HIGH, true, null);
        assertThat(contact.isPrimary()).isTrue();

        contact.demotePrimary();
        assertThat(contact.isPrimary()).isFalse();
    }

    @Test
    @DisplayName("updateLastContacted() sets lastContactedAt")
    void updateLastContacted() {
        Contact contact = newContact();
        assertThat(contact.getLastContactedAt()).isNull();

        contact.updateLastContacted();
        assertThat(contact.getLastContactedAt()).isNotNull();
    }

    @Test
    @DisplayName("updateProfile() updates all fields")
    void updateProfile() {
        Contact contact = newContact();

        contact.updateProfile("Jane", "Smith", "jane@acme.com", "+1555",
                Role.DIRECTOR, InfluenceLevel.MEDIUM, "Updated notes");

        assertThat(contact.getFirstName()).isEqualTo("Jane");
        assertThat(contact.getLastName()).isEqualTo("Smith");
        assertThat(contact.getEmail()).isEqualTo("jane@acme.com");
        assertThat(contact.getRole()).isEqualTo(Role.DIRECTOR);
    }

    @Test
    @DisplayName("rehydrate() restores all fields from persistence")
    void rehydrate() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Contact contact = Contact.rehydrate(id, TENANT, CLIENT,
                "John", "Doe", "john@acme.com", "+33600000000",
                Role.CEO, InfluenceLevel.HIGH, true, now,
                "notes", now, now);

        assertThat(contact.getId()).isEqualTo(id);
        assertThat(contact.getLastContactedAt()).isEqualTo(now);
        assertThat(contact.isPrimary()).isTrue();
        assertThat(contact.getCreatedAt()).isEqualTo(now);
    }
}
