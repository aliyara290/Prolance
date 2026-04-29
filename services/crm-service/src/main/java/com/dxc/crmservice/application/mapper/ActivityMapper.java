package com.dxc.crmservice.application.mapper;

import com.dxc.crmservice.application.dto.activity.req.CreateActivityRequest;
import com.dxc.crmservice.application.dto.activity.res.ActivityResponse;
import com.dxc.crmservice.domain.model.entity.Activity;
import org.mapstruct.Mapper;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    default Activity toDomain(CreateActivityRequest request, UUID tenantId) {
        if (request == null) return null;
        return Activity.create(
                tenantId,
                request.type(),
                request.subject(),
                request.description(),
                request.scheduledAt(),
                request.userId(),
                request.entityId(),
                request.entityType()
        );
    }

    default ActivityResponse toResponse(Activity activity) {
        if (activity == null) return null;
        return new ActivityResponse(
                activity.getId(),
                activity.getType(),
                activity.getSubject(),
                activity.getDescription(),
                activity.getScheduledAt(),
                activity.getCompletedAt(),
                activity.getUserId(),
                activity.getEntityId(),
                activity.getEntityType(),
                activity.getCreatedAt(),
                activity.getUpdatedAt()
        );
    }
}