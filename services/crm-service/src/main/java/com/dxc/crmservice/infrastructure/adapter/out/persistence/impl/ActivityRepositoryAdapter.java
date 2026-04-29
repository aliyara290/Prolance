package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.ActivityRepository;
import com.dxc.crmservice.domain.model.entity.Activity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.ActivityEntity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.ActivityRepositoryJpa;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper.ActivityPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ActivityRepositoryAdapter implements ActivityRepository {

    private final ActivityRepositoryJpa activityRepositoryJpa;
    private final ActivityPersistenceMapper activityPersistenceMapper;

    @Override
    public Activity save(Activity domain) {
        ActivityEntity entity = activityPersistenceMapper.toEntity(domain);
        ActivityEntity saved = activityRepositoryJpa.save(entity);
        return activityPersistenceMapper.toDomain(saved);
    }

    @Override
    public Activity findById(UUID id, UUID tenantId) {
        return activityRepositoryJpa.findByIdAndTenantId(id, tenantId)
                .map(activityPersistenceMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Activity update(Activity domain) {
        if (!activityRepositoryJpa.existsById(domain.getId())) {
            throw new IllegalArgumentException("Activity with ID " + domain.getId() + " does not exist");
        }
        ActivityEntity entity = activityPersistenceMapper.toEntity(domain);
        ActivityEntity updated = activityRepositoryJpa.save(entity);
        return activityPersistenceMapper.toDomain(updated);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        if (activityRepositoryJpa.existsByIdAndTenantId(id, tenantId)) {
            activityRepositoryJpa.deleteById(id);
        } else {
            throw new IllegalArgumentException("Activity with ID " + id + " does not exist for the given tenant");
        }
    }

    @Override
    public Page<Activity> findAll(UUID tenantId, Pageable pageable) {
        return activityRepositoryJpa.findAllByTenantId(tenantId, pageable)
                .map(activityPersistenceMapper::toDomain);
    }
}
