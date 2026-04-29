package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.dto.activity.req.CreateActivityRequest;
import com.dxc.crmservice.application.dto.activity.req.UpdateActivityRequest;
import com.dxc.crmservice.application.dto.activity.res.ActivityResponse;
import com.dxc.crmservice.application.mapper.ActivityMapper;
import com.dxc.crmservice.application.port.in.ActivityUseCase;
import com.dxc.crmservice.application.port.out.ActivityRepository;
import com.dxc.crmservice.application.utils.Utils;
import com.dxc.crmservice.domain.exception.ServiceLogicException;
import com.dxc.crmservice.domain.model.entity.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ActivityService implements ActivityUseCase {

    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    @Override
    public ActivityResponse createActivity(CreateActivityRequest request) {
        UUID tenantId = Utils.resolveTenantId();
        try {
            Activity activity = activityMapper.toDomain(request, tenantId);
            activityRepository.save(activity);
            return activityMapper.toResponse(activity);
        } catch (Exception e) {
            log.error("Error creating activity: {}", e.getMessage());
            throw new ServiceLogicException("Failed to create activity");
        }
    }

    @Override
    public ActivityResponse updateActivity(UUID id, UpdateActivityRequest request) {
        UUID tenantId = Utils.resolveTenantId();
        try {
            Activity activity = activityRepository.findById(id, tenantId);
            if (activity == null) {
                throw new ServiceLogicException("Activity not found");
            }

            activity.updateDetails(
                    request.subject() != null ? request.subject() : activity.getSubject(),
                    request.description() != null ? request.description() : activity.getDescription()
            );

            if (request.scheduledAt() != null) {
                activity.reschedule(request.scheduledAt());
            }

            Activity updatedActivity = activityRepository.update(activity);
            return activityMapper.toResponse(updatedActivity);
        } catch (Exception e) {
            log.error("Error updating activity: {}", e.getMessage());
            throw new ServiceLogicException("Failed to update activity: " + e.getMessage());
        }
    }

    @Override
    public void deleteActivity(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        try {
            activityRepository.delete(id, tenantId);
        } catch (Exception e) {
            log.error("Error deleting activity: {}", e.getMessage());
            throw new ServiceLogicException("Failed to delete activity");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityResponse getActivity(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        Activity activity = activityRepository.findById(id, tenantId);
        if (activity == null) {
            throw new ServiceLogicException("Activity not found");
        }
        return activityMapper.toResponse(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityResponse> getAllActivities(Pageable pageable) {
        UUID tenantId = Utils.resolveTenantId();
        Page<Activity> activities = activityRepository.findAll(tenantId, pageable);
        return activities.map(activityMapper::toResponse);
    }

    @Override
    public ActivityResponse completeActivity(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        Activity activity = activityRepository.findById(id, tenantId);
        if (activity == null) {
            throw new ServiceLogicException("Activity not found");
        }
        activity.complete();
        activityRepository.update(activity);
        return activityMapper.toResponse(activity);
    }

    @Override
    public ActivityResponse rescheduleActivity(UUID id, LocalDateTime newDate) {
        UUID tenantId = Utils.resolveTenantId();
        Activity activity = activityRepository.findById(id, tenantId);
        if (activity == null) {
            throw new ServiceLogicException("Activity not found");
        }
        activity.reschedule(newDate);
        activityRepository.update(activity);
        return activityMapper.toResponse(activity);
    }
}
