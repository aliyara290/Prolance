package com.dxc.crmservice.domain.model.aggregate;

import com.dxc.crmservice.domain.exception.BusinessRuleViolationException;
import com.dxc.crmservice.domain.exception.InvalidStateTransitionException;
import com.dxc.crmservice.domain.exception.ValidationException;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Opportunity – Domain Aggregate")
class OpportunityTest {

    private static final UUID TENANT    = UUID.randomUUID();
    private static final UUID CLIENT_ID = UUID.randomUUID();

    private Opportunity newOpp() {
        return Opportunity.create(TENANT, CLIENT_ID, "Big deal", "desc",
                50_000.0, 60_000.0, 50, Stage.PROSPECTING, Priority.HIGH);
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("creates opportunity with PROSPECTING stage and generated id")
        void happyPath() {
            Opportunity opp = newOpp();

            assertThat(opp.getId()).isNotNull();
            assertThat(opp.getStage()).isEqualTo(Stage.PROSPECTING);
            assertThat(opp.getTitle()).isEqualTo("Big deal");
        }

        @Test
        @DisplayName("rejects blank title")
        void blankTitle() {
            assertThatThrownBy(() ->
                    Opportunity.create(TENANT, CLIENT_ID, "", "desc", 0.0, 0.0, 0, Stage.PROSPECTING, Priority.LOW))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("rejects negative budget")
        void negativeBudget() {
            assertThatThrownBy(() ->
                    Opportunity.create(TENANT, CLIENT_ID, "title", "desc", -1.0, 0.0, 0, Stage.PROSPECTING, Priority.LOW))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("rejects probability > 100")
        void invalidProbability() {
            assertThatThrownBy(() ->
                    Opportunity.create(TENANT, CLIENT_ID, "title", "desc", 0.0, 0.0, 101, Stage.PROSPECTING, Priority.LOW))
                    .isInstanceOf(BusinessRuleViolationException.class);
        }
    }

    @Nested
    @DisplayName("moveStage()")
    class MoveStage {

        @Test
        @DisplayName("PROSPECTING → QUALIFICATION is valid")
        void prospectingToQualification() {
            Opportunity opp = newOpp();
            opp.moveStage(Stage.QUALIFICATION);

            assertThat(opp.getStage()).isEqualTo(Stage.QUALIFICATION);
        }

        @Test
        @DisplayName("PROSPECTING → PROPOSAL is invalid")
        void invalidSkip() {
            Opportunity opp = newOpp();

            assertThatThrownBy(() -> opp.moveStage(Stage.PROPOSAL))
                    .isInstanceOf(InvalidStateTransitionException.class);
        }

        @Test
        @DisplayName("rejects null stage")
        void nullStage() {
            Opportunity opp = newOpp();

            assertThatThrownBy(() -> opp.moveStage(null))
                    .isInstanceOf(ValidationException.class);
        }
    }

    @Test
    @DisplayName("markAsWon() sets stage to WON and records closingDate")
    void markAsWon() {
        Opportunity opp = newOpp();
        opp.markAsWon();

        assertThat(opp.getStage()).isEqualTo(Stage.WON);
        assertThat(opp.getClosingDate()).isNotNull();
    }

    @Test
    @DisplayName("markAsLost() sets stage to LOST and records reason")
    void markAsLost() {
        Opportunity opp = newOpp();
        opp.markAsLost("Budget cut");

        assertThat(opp.getStage()).isEqualTo(Stage.LOST);
        assertThat(opp.getLostReason()).isEqualTo("Budget cut");
    }

    @Test
    @DisplayName("markAsLost() rejects blank reason")
    void markAsLostBlankReason() {
        Opportunity opp = newOpp();

        assertThatThrownBy(() -> opp.markAsLost("  "))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("cannot modify a WON opportunity")
    void cannotModifyWon() {
        Opportunity opp = newOpp();
        opp.markAsWon();

        assertThatThrownBy(() -> opp.moveStage(Stage.QUALIFICATION))
                .isInstanceOf(BusinessRuleViolationException.class);
    }
}
