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
public class ActivityRepositoryAdapter  {

    private final ActivityRepositoryJpa activityRepositoryJpa;
    private final ActivityPersistenceMapper activityPersistenceMapper;
}
