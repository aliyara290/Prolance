package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.OpportunityRepository;
import com.dxc.crmservice.domain.model.aggregate.Opportunity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.OpportunityEntity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.OpportunityRepositoryJpa;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper.OpportunityPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpportunityRepositoryAdapter {

    private final OpportunityRepositoryJpa opportunityRepositoryJpa;
    private final OpportunityPersistenceMapper opportunityPersistenceMapper;


}
