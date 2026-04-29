package com.dxc.crmservice.application.port.in;

import com.dxc.crmservice.application.dto.activity.req.CreateActivityRequest;
import com.dxc.crmservice.application.dto.activity.req.UpdateActivityRequest;
import com.dxc.crmservice.application.dto.activity.res.ActivityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ActivityUseCase {

    ActivityResponse createActivity(CreateActivityRequest request);
    ActivityResponse updateActivity(UUID id, UpdateActivityRequest request);
    void deleteActivity(UUID id);
    ActivityResponse getActivity(UUID id);
    Page<ActivityResponse> getAllActivities(Pageable pageable);
    
    ActivityResponse completeActivity(UUID id);
    ActivityResponse rescheduleActivity(UUID id, LocalDateTime newDate);
}
