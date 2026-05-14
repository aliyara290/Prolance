package com.dxc.crmservice.domain.model.aggregate;

import com.dxc.crmservice.domain.exception.BusinessRuleViolationException;
import com.dxc.crmservice.domain.exception.InvalidStateTransitionException;
import com.dxc.crmservice.domain.exception.ValidationException;
import com.dxc.crmservice.domain.model.valueobject.LeadStatus;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Source;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Lead – Domain Aggregate")
class LeadTest {

    private static final UUID TENANT = UUID.randomUUID();

    // helpers

    private Lead newLead() {
        return Lead.create(TENANT, "Acme deal", "description", Source.WEBSITE, Priority.MEDIUM);
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("creates lead with NEW status and generated id")
        void happyPath() {
            Lead lead = newLead();

            assertThat(lead.getId()).isNotNull();
            assertThat(lead.getTenantId()).isEqualTo(TENANT);
            assertThat(lead.getStatus()).isEqualTo(LeadStatus.NEW);
            assertThat(lead.getTitle()).isEqualTo("Acme deal");
            assertThat(lead.getScore()).isZero();
        }

        @Test
        @DisplayName("rejects blank title")
        void blankTitle() {
            assertThatThrownBy(() ->
                    Lead.create(TENANT, "  ", "desc", Source.WEBSITE, Priority.LOW))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("rejects null title")
        void nullTitle() {
            assertThatThrownBy(() ->
                    Lead.create(TENANT, null, "desc", Source.WEBSITE, Priority.LOW))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("rejects null tenantId")
        void nullTenantId() {
            assertThatThrownBy(() ->
                    Lead.create(null, "title", "desc", Source.WEBSITE, Priority.LOW))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("markAsContacted()")
    class MarkAsContacted {

        @Test
        @DisplayName("transitions NEW → CONTACTED and sets firstContactedAt")
        void success() {
            Lead lead = newLead();
            lead.markAsContacted();

            assertThat(lead.getStatus()).isEqualTo(LeadStatus.CONTACTED);
            assertThat(lead.getFirstContactedAt()).isNotNull();
        }

        @Test
        @DisplayName("fails when status is not NEW")
        void wrongStatus() {
            Lead lead = newLead();
            lead.markAsContacted();

            assertThatThrownBy(lead::markAsContacted)
                    .isInstanceOf(InvalidStateTransitionException.class);
        }
    }

    @Nested
    @DisplayName("qualify()")
    class Qualify {

        @Test
        @DisplayName("transitions CONTACTED → QUALIFIED")
        void success() {
            Lead lead = newLead();
            lead.markAsContacted();
            lead.qualify();

            assertThat(lead.getStatus()).isEqualTo(LeadStatus.QUALIFIED);
        }

        @Test
        @DisplayName("fails when not CONTACTED")
        void wrongStatus() {
            Lead lead = newLead();

            assertThatThrownBy(lead::qualify)
                    .isInstanceOf(InvalidStateTransitionException.class);
        }
    }

    @Nested
    @DisplayName("markAsUnqualified()")
    class MarkAsUnqualified {

        @Test
        @DisplayName("records reason and transitions to UNQUALIFIED")
        void success() {
            Lead lead = newLead();
            lead.markAsUnqualified("Not a fit");

            assertThat(lead.getStatus()).isEqualTo(LeadStatus.UNQUALIFIED);
            assertThat(lead.getUnqualifiedReason()).isEqualTo("Not a fit");
        }

        @Test
        @DisplayName("rejects blank reason")
        void blankReason() {
            Lead lead = newLead();

            assertThatThrownBy(() -> lead.markAsUnqualified("  "))
                    .isInstanceOf(ValidationException.class);
        }
    }

    @Test
    @DisplayName("assignClient() stores clientId")
    void assignClient() {
        Lead lead = newLead();
        UUID clientId = UUID.randomUUID();
        lead.assignClient(clientId);

        assertThat(lead.getClientId()).isEqualTo(clientId);
    }

    @Test
    @DisplayName("addContact() stores contactId")
    void addContact() {
        Lead lead = newLead();
        UUID contactId = UUID.randomUUID();
        lead.addContact(contactId);

        assertThat(lead.getContactId()).isEqualTo(contactId);
    }

    @Test
    @DisplayName("updateScore() accepts valid values")
    void updateScore() {
        Lead lead = newLead();
        lead.updateScore(80);

        assertThat(lead.getScore()).isEqualTo(80);
    }

    @Test
    @DisplayName("updateScore() rejects negative values")
    void negativeScore() {
        Lead lead = newLead();

        assertThatThrownBy(() -> lead.updateScore(-1))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("cannot modify a QUALIFIED (closed) lead")
    void cannotModifyQualifiedLead() {
        Lead lead = newLead();
        lead.markAsContacted();
        lead.qualify();

        assertThatThrownBy(() -> lead.updateDetails("new", "desc", Source.REFERRAL, Priority.HIGH))
                .isInstanceOf(BusinessRuleViolationException.class);
    }
}
