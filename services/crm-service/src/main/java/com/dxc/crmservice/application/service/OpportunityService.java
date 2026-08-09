package com.dxc.crmservice.application.service;

import com.dxc.crmservice.application.dto.opportunity.req.CreateOpportunityRequest;
import com.dxc.crmservice.application.dto.opportunity.req.UpdateOpportunityRequest;
import com.dxc.crmservice.application.dto.opportunity.res.OpportunityResponse;
import com.dxc.crmservice.application.mapper.OpportunityMapper;
import com.dxc.crmservice.application.port.in.OpportunityUseCase;
import com.dxc.crmservice.application.port.out.OpportunityRepository;
import com.dxc.crmservice.application.security.TenantGuard;
import com.dxc.crmservice.application.utils.Utils;
import com.dxc.crmservice.domain.exception.ServiceLogicException;
import com.dxc.crmservice.domain.model.aggregate.Opportunity;
import com.dxc.crmservice.domain.model.valueobject.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import com.dxc.crmservice.domain.event.AuditLogEvent;
import com.dxc.crmservice.domain.model.valueobject.AuditAction;
import com.dxc.crmservice.domain.model.valueobject.EntityType;
import com.dxc.crmservice.infrastructure.config.TenantContextHolder;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OpportunityService implements OpportunityUseCase {

    private final TenantGuard tenantGuard;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityMapper opportunityMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public OpportunityResponse createOpportunity(CreateOpportunityRequest request) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        try {
            Opportunity opportunity = opportunityMapper.toDomain(request, tenantId);
            opportunityRepository.save(opportunity);
            
            eventPublisher.publishEvent(new AuditLogEvent(
                    tenantId,
                    TenantContextHolder.getUserId(),
                    AuditAction.CREATE,
                    EntityType.OPPORTUNITY,
                    opportunity.getId(),
                    "Opportunity '" + opportunity.getTitle() + "' created"
            ));
            
            return opportunityMapper.toResponse(opportunity);
        } catch (Exception e) {
            log.error("Error creating opportunity: {}", e.getMessage());
            throw new ServiceLogicException("Failed to create opportunity");
        }
    }

    @Override
    public OpportunityResponse updateOpportunity(UUID id, UpdateOpportunityRequest request) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        try {
            Opportunity opportunity = opportunityRepository.findById(id, tenantId);
            if (opportunity == null) {
                throw new ServiceLogicException("Opportunity not found");
            }

            opportunity.updateDetails(
                    request.name() != null ? request.name() : opportunity.getTitle(),
                    opportunity.getDescription(),
                    request.amount() != null ? request.amount().doubleValue() : opportunity.getEstimatedBudget(),
                    opportunity.getExpectedRevenue(),
                    opportunity.getProbability(),
                    opportunity.getExpectedStartDate(),
                    opportunity.getExpectedEndDate()
            );

            if (request.stage() != null) {
                opportunity.moveStage(request.stage());
            }

            Opportunity updatedOpportunity = opportunityRepository.update(opportunity);
            
            eventPublisher.publishEvent(new AuditLogEvent(
                    tenantId,
                    TenantContextHolder.getUserId(),
                    AuditAction.UPDATE,
                    EntityType.OPPORTUNITY,
                    opportunity.getId(),
                    "Opportunity '" + opportunity.getTitle() + "' updated"
            ));
            
            return opportunityMapper.toResponse(updatedOpportunity);
        } catch (Exception e) {
            log.error("Error updating opportunity: {}", e.getMessage());
            throw new ServiceLogicException("Failed to update opportunity: " + e.getMessage());
        }
    }

    @Override
    public void deleteOpportunity(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        try {
            opportunityRepository.delete(id, tenantId);
            
            eventPublisher.publishEvent(new AuditLogEvent(
                    tenantId,
                    TenantContextHolder.getUserId(),
                    AuditAction.DELETE,
                    EntityType.OPPORTUNITY,
                    id,
                    "Opportunity deleted"
            ));
        } catch (Exception e) {
            log.error("Error deleting opportunity: {}", e.getMessage());
            throw new ServiceLogicException("Failed to delete opportunity");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OpportunityResponse getOpportunity(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        Opportunity opportunity = opportunityRepository.findById(id, tenantId);
        if (opportunity == null) {
            throw new ServiceLogicException("Opportunity not found");
        }
        return opportunityMapper.toResponse(opportunity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OpportunityResponse> getAllOpportunities(Pageable pageable) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        Page<Opportunity> opportunities = opportunityRepository.findAll(tenantId, pageable);
        return opportunities.map(opportunityMapper::toResponse);
    }

    @Override
    public OpportunityResponse moveStage(UUID id, Stage stage) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        Opportunity opportunity = opportunityRepository.findById(id, tenantId);
        if (opportunity == null) {
            throw new ServiceLogicException("Opportunity not found");
        }
        opportunity.moveStage(stage);
        opportunityRepository.update(opportunity);
        
        eventPublisher.publishEvent(new AuditLogEvent(
                tenantId,
                TenantContextHolder.getUserId(),
                AuditAction.UPDATE,
                EntityType.OPPORTUNITY,
                id,
                "Opportunity moved to stage " + stage
        ));
        
        return opportunityMapper.toResponse(opportunity);
    }

    @Override
    public OpportunityResponse markAsWon(UUID id) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        Opportunity opportunity = opportunityRepository.findById(id, tenantId);
        if (opportunity == null) {
            throw new ServiceLogicException("Opportunity not found");
        }
        opportunity.markAsWon();
        opportunityRepository.update(opportunity);
        
        eventPublisher.publishEvent(new AuditLogEvent(
                tenantId,
                TenantContextHolder.getUserId(),
                AuditAction.UPDATE,
                EntityType.OPPORTUNITY,
                id,
                "Opportunity marked as WON"
        ));
        
        return opportunityMapper.toResponse(opportunity);
    }

    @Override
    public OpportunityResponse markAsLost(UUID id, String reason) {
        UUID tenantId = Utils.resolveTenantId();
        tenantGuard.ensureTenantIsActive(tenantId);
        Opportunity opportunity = opportunityRepository.findById(id, tenantId);
        if (opportunity == null) {
            throw new ServiceLogicException("Opportunity not found");
        }
        opportunity.markAsLost(reason);
        opportunityRepository.update(opportunity);
        
        eventPublisher.publishEvent(new AuditLogEvent(
                tenantId,
                TenantContextHolder.getUserId(),
                AuditAction.UPDATE,
                EntityType.OPPORTUNITY,
                id,
                "Opportunity marked as LOST: " + reason
        ));
        
        return opportunityMapper.toResponse(opportunity);
    }
}
