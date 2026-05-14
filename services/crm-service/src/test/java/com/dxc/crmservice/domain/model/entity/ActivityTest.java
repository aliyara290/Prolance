package com.dxc.crmservice.domain.model.entity;

import com.dxc.crmservice.domain.exception.BusinessRuleViolationException;
import com.dxc.crmservice.domain.exception.ValidationException;
import com.dxc.crmservice.domain.model.valueobject.ActivityType;
import com.dxc.crmservice.domain.model.valueobject.EntityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Activity – Domain Entity")
class ActivityTest {

    private static final UUID TENANT    = UUID.randomUUID();
    private static final UUID USER_ID   = UUID.randomUUID();
    private static final UUID ENTITY_ID = UUID.randomUUID();

    private Activity newActivity() {
        return Activity.create(
                TENANT,
                ActivityType.CALL,
                "Follow-up call",
                "Discussing renewal",
                LocalDateTime.now().plusDays(1),
                USER_ID,
                ENTITY_ID,
                EntityType.LEAD
        );
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("creates activity with generated id and null completedAt")
        void happyPath() {
            Activity activity = newActivity();

            assertThat(activity.getId()).isNotNull();
            assertThat(activity.getCompletedAt()).isNull();
            assertThat(activity.getSubject()).isEqualTo("Follow-up call");
        }

        @Test
        @DisplayName("rejects blank subject")
        void blankSubject() {
            assertThatThrownBy(() ->
                    Activity.create(TENANT, ActivityType.CALL, "  ", null,
                            LocalDateTime.now().plusDays(1), USER_ID, ENTITY_ID, EntityType.LEAD))
                    .isInstanceOf(ValidationException.class);
        }
    }

    @Test
    @DisplayName("complete() sets completedAt")
    void complete() {
        Activity activity = newActivity();
        activity.complete();

        assertThat(activity.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("complete() twice throws ValidationException")
    void completeTwice() {
        Activity activity = newActivity();
        activity.complete();

        assertThatThrownBy(activity::complete)
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("reschedule() updates scheduledAt")
    void reschedule() {
        Activity activity = newActivity();
        LocalDateTime newDate = LocalDateTime.now().plusDays(5);
        activity.reschedule(newDate);

        assertThat(activity.getScheduledAt()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("reschedule() rejects past date")
    void rescheduleInPast() {
        Activity activity = newActivity();

        assertThatThrownBy(() -> activity.reschedule(LocalDateTime.now().minusDays(1)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("reschedule() on completed activity throws BusinessRuleViolationException")
    void rescheduleCompleted() {
        Activity activity = newActivity();
        activity.complete();

        assertThatThrownBy(() -> activity.reschedule(LocalDateTime.now().plusDays(3)))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    @DisplayName("updateDetails() on completed activity throws BusinessRuleViolationException")
    void updateDetailsCompleted() {
        Activity activity = newActivity();
        activity.complete();

        assertThatThrownBy(() -> activity.updateDetails("New subject", null))
                .isInstanceOf(BusinessRuleViolationException.class);
    }
}
