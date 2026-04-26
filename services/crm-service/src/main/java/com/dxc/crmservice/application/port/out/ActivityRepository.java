package com.dxc.crmservice.application.port.out;

import com.dxc.crmservice.domain.model.entity.Activity;

import java.util.List;
import java.util.UUID;

public interface ActivityRepository {
    Activity save(Activity activity);
    Activity findById(UUID id);
    Activity update(Activity activity);
    void delete(UUID id, UUID tenantId);
    List<Activity> findAll(UUID tenantId);
}