package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.ActivityRepository;
import com.dxc.crmservice.domain.model.entity.Activity;

import java.util.List;
import java.util.UUID;

public class ActivityRepositoryAdapter implements ActivityRepository {

    @Override
    public Activity save(Activity activity) {
        return null;
    }

    @Override
    public Activity findById(UUID id) {
        return null;
    }

    @Override
    public Activity update(Activity activity) {
        return null;
    }

    @Override
    public void delete(UUID id, UUID tenantId) {

    }

    @Override
    public List<Activity> findAll(UUID tenantId) {
        return List.of();
    }
}
