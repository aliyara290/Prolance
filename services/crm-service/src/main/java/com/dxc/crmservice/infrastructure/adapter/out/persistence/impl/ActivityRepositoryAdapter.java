package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.ActivityRepository;
import com.dxc.crmservice.domain.model.entity.Activity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.ActivityEntity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.ActivityRepositoryJpa;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper.ActivityPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ActivityRepositoryAdapter implements ActivityRepository {

    private final ActivityRepositoryJpa activityRepositoryJpa;
    private final ActivityPersistenceMapper activityPersistenceMapper;

    @Override
    public Activity save(Activity activity) {
        ActivityEntity entity = activityPersistenceMapper.toEntity(activity);
        return activityPersistenceMapper.toDomain(activityRepositoryJpa.save(entity));
    }

    @Override
    public Activity findById(UUID id) {
        return activityRepositoryJpa.findById(id)
                .map(activityPersistenceMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Activity update(Activity activity) {
        return save(activity);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        activityRepositoryJpa.findByIdAndTenantId(id, tenantId).ifPresent(activityRepositoryJpa::delete);
    }

    @Override
    public List<Activity> findAll(UUID tenantId) {
        return activityRepositoryJpa.findByTenantId(tenantId).stream()
                .map(activityPersistenceMapper::toDomain)
                .toList();
    }
}
