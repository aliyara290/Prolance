package com.dxc.notificationservice.application.mapper;

import com.dxc.notificationservice.application.dto.notification.res.NotificationResponse;
import com.dxc.notificationservice.domain.model.aggregate.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NotificationApplicationMapper {
    NotificationResponse toResponse(Notification notification);
}
