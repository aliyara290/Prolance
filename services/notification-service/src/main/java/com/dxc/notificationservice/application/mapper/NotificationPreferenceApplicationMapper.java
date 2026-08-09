package com.dxc.notificationservice.application.mapper;

import com.dxc.notificationservice.application.dto.preference.res.NotificationPreferenceResponse;
import com.dxc.notificationservice.domain.model.entity.NotificationPreference;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NotificationPreferenceApplicationMapper {
    NotificationPreferenceResponse toResponse(NotificationPreference preference);
}
