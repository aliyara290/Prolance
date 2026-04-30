package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.dto.activity.req.CreateActivityRequest;
import com.dxc.crmservice.application.dto.activity.req.UpdateActivityRequest;
import com.dxc.crmservice.application.dto.activity.res.ActivityResponse;
import com.dxc.crmservice.application.mapper.ActivityMapper;
import com.dxc.crmservice.application.port.out.ActivityRepository;
import com.dxc.crmservice.domain.exception.ServiceLogicException;
import com.dxc.crmservice.domain.model.entity.Activity;
import com.dxc.crmservice.domain.model.valueobject.ActivityType;
import com.dxc.crmservice.domain.model.valueobject.EntityType;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActivityService – Application Layer")
class ActivityServiceTest {

    @Mock
    ActivityRepository activityRepository;
    @Mock
    ActivityMapper activityMapper;

    @InjectMocks
    ActivityService activityService;

    private static final UUID TENANT_ID = UUID.randomUUID();
    private static final UUID ENTITY_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ACTIVITY_ID = UUID.randomUUID();

    @BeforeEach
    void setTenant() {
        TenantContextHolder.setTenantId(TENANT_ID.toString());
    }

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
    }

    // helpers

    private Activity stubActivity() {
        return Activity.create(TENANT_ID, ActivityType.CALL, "Call subject", "desc", LocalDateTime.now().plusDays(1), USER_ID, ENTITY_ID, EntityType.LEAD);
    }

    private ActivityResponse stubResponse(Activity a) {
        return new ActivityResponse(a.getId(), a.getType(), a.getSubject(), a.getDescription(), a.getScheduledAt(), a.getCompletedAt(), a.getUserId(), a.getEntityId(), a.getEntityType(), a.getCreatedAt(), a.getUpdatedAt());
    }

    private CreateActivityRequest createRequest() {
        return new CreateActivityRequest(ActivityType.CALL, "Call subject", "desc", LocalDateTime.now().plusDays(1), USER_ID, ENTITY_ID, EntityType.LEAD);
    }

    @Nested
    @DisplayName("createActivity()")
    class CreateActivity {

        @Test
        @DisplayName("saves activity and returns response")
        void success() {
            Activity activity = stubActivity();
            ActivityResponse response = stubResponse(activity);

            when(activityMapper.toDomain(any(), eq(TENANT_ID))).thenReturn(activity);
            when(activityMapper.toResponse(activity)).thenReturn(response);

            ActivityResponse result = activityService.createActivity(createRequest());

            assertThat(result).isEqualTo(response);
            verify(activityRepository).save(activity);
        }
    }

    @Nested
    @DisplayName("updateActivity()")
    class UpdateActivity {

        @Test
        @DisplayName("updates subject and description then returns updated response")
        void success() {
            Activity activity = stubActivity();
            Activity updated = stubActivity();
            ActivityResponse response = stubResponse(updated);

            when(activityRepository.findById(ACTIVITY_ID, TENANT_ID)).thenReturn(activity);
            when(activityRepository.update(activity)).thenReturn(updated);
            when(activityMapper.toResponse(updated)).thenReturn(response);

            UpdateActivityRequest req = new UpdateActivityRequest(ActivityType.CALL, "New subject", "New desc", null, null);
            ActivityResponse result = activityService.updateActivity(ACTIVITY_ID, req);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws ServiceLogicException when activity not found")
        void notFound() {
            when(activityRepository.findById(ACTIVITY_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() -> activityService.updateActivity(ACTIVITY_ID, new UpdateActivityRequest(ActivityType.CALL, "s", null, null, null))).isInstanceOf(ServiceLogicException.class);
        }
    }

    @Nested
    @DisplayName("getActivity()")
    class GetActivity {

        @Test
        @DisplayName("returns activity response for existing id")
        void success() {
            Activity activity = stubActivity();
            ActivityResponse response = stubResponse(activity);

            when(activityRepository.findById(ACTIVITY_ID, TENANT_ID)).thenReturn(activity);
            when(activityMapper.toResponse(activity)).thenReturn(response);

            ActivityResponse result = activityService.getActivity(ACTIVITY_ID);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws ServiceLogicException when not found")
        void notFound() {
            when(activityRepository.findById(ACTIVITY_ID, TENANT_ID)).thenReturn(null);

            assertThatThrownBy(() -> activityService.getActivity(ACTIVITY_ID)).isInstanceOf(ServiceLogicException.class);
        }
    }

    @Test
    @DisplayName("deleteActivity() delegates to repository")
    void deleteActivity() {
        activityService.deleteActivity(ACTIVITY_ID);

        verify(activityRepository).delete(ACTIVITY_ID, TENANT_ID);
    }

    @Test
    @DisplayName("getAllActivities() maps page correctly")
    void getAllActivities() {
        Activity activity = stubActivity();
        ActivityResponse response = stubResponse(activity);
        Page<Activity> page = new PageImpl<>(List.of(activity));

        when(activityRepository.findAll(TENANT_ID, PageRequest.of(0, 10))).thenReturn(page);
        when(activityMapper.toResponse(activity)).thenReturn(response);

        Page<ActivityResponse> result = activityService.getAllActivities(PageRequest.of(0, 10));

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("completeActivity() completes and returns response")
    void completeActivity() {
        Activity activity = stubActivity();
        ActivityResponse response = stubResponse(activity);

        when(activityRepository.findById(ACTIVITY_ID, TENANT_ID)).thenReturn(activity);
        when(activityMapper.toResponse(activity)).thenReturn(response);

        ActivityResponse result = activityService.completeActivity(ACTIVITY_ID);

        assertThat(result).isEqualTo(response);
        assertThat(activity.getCompletedAt()).isNotNull();
        verify(activityRepository).update(activity);
    }

    @Test
    @DisplayName("rescheduleActivity() reschedules and returns response")
    void rescheduleActivity() {
        Activity activity = stubActivity();
        ActivityResponse response = stubResponse(activity);
        LocalDateTime newDate = LocalDateTime.now().plusDays(7);

        when(activityRepository.findById(ACTIVITY_ID, TENANT_ID)).thenReturn(activity);
        when(activityMapper.toResponse(activity)).thenReturn(response);

        ActivityResponse result = activityService.rescheduleActivity(ACTIVITY_ID, newDate);

        assertThat(result).isEqualTo(response);
        assertThat(activity.getScheduledAt()).isEqualTo(newDate);
        verify(activityRepository).update(activity);
    }
}
