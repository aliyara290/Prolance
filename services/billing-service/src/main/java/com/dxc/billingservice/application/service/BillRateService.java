package com.dxc.billingservice.application.service;

import com.dxc.billingservice.application.dto.req.CreateBillRateRequest;
import com.dxc.billingservice.application.dto.req.UpdateBillRateRequest;
import com.dxc.billingservice.application.dto.res.BillRateResponse;
import com.dxc.billingservice.application.mapper.BillingMapper;
import com.dxc.billingservice.application.port.in.BillRateUseCase;
import com.dxc.billingservice.application.port.out.BillRateRepository;
import com.dxc.billingservice.domain.exception.BusinessRuleException;
import com.dxc.billingservice.domain.model.entity.BillRate;
import com.dxc.billingservice.infrastructure.config.TenantContextHolder;
import com.dxc.billingservice.application.port.out.feign.TenantFeignPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BillRateService implements BillRateUseCase {

    private final BillRateRepository billRateRepository;
    private final BillingMapper mapper;
    private final TenantFeignPort tenantFeignPort;

    @Override
    public BillRateResponse createBillRate(CreateBillRateRequest request) {
        UUID tenantId = getTenantId();
        UUID userId = getUserId();

        billRateRepository.findByProjectIdAndUserId(request.projectId(), request.userId(), tenantId)
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Bill rate already exists for user " + request.userId() + " on project " + request.projectId());
                });

        var tenantUser = tenantFeignPort.getUser(request.userId());
        
        var seniorityLevel = request.seniorityLevel() != null ? request.seniorityLevel() 
                : (tenantUser != null ? tenantUser.seniorityLevel() : null);
        var educationLevel = request.educationLevel() != null ? request.educationLevel() 
                : (tenantUser != null ? tenantUser.educationLevel() : null);
                
        var hourlyRate = request.hourlyRate();
        if (hourlyRate == null && tenantUser != null && tenantUser.baseHourlySalary() != null) {
            // Apply a default 2x multiplier for billing client vs base salary
            hourlyRate = tenantUser.baseHourlySalary().multiply(java.math.BigDecimal.valueOf(2));
        }

        BillRate billRate = BillRate.create(
                tenantId,
                request.projectId(),
                request.userId(),
                seniorityLevel,
                educationLevel,
                hourlyRate,
                request.dailyRate(),
                request.effectiveFrom(),
                request.effectiveTo(),
                userId
        );

        BillRate saved = billRateRepository.save(billRate);
        log.info("Bill rate created for user {} on project {}", request.userId(), request.projectId());
        return mapper.toBillRateResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BillRateResponse getBillRate(UUID billRateId) {
        BillRate billRate = findBillRate(billRateId);
        return mapper.toBillRateResponse(billRate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillRateResponse> getBillRatesByProject(UUID projectId) {
        UUID tenantId = getTenantId();
        List<BillRate> rates = billRateRepository.findByProjectId(projectId, tenantId);
        return mapper.toBillRateResponses(rates);
    }

    @Override
    @Transactional(readOnly = true)
    public BillRateResponse getBillRateByProjectAndUser(UUID projectId, UUID userId) {
        UUID tenantId = getTenantId();
        BillRate rate = billRateRepository.findByProjectIdAndUserId(projectId, userId, tenantId)
                .orElseThrow(() -> new BusinessRuleException("Bill rate not found for user " + userId + " on project " + projectId));
        return mapper.toBillRateResponse(rate);
    }

    @Override
    public BillRateResponse updateBillRate(UUID billRateId, UpdateBillRateRequest request) {
        BillRate billRate = findBillRate(billRateId);
        billRate.update(
                request.seniorityLevel(),
                request.educationLevel(),
                request.hourlyRate(),
                request.dailyRate(),
                request.effectiveFrom(),
                request.effectiveTo()
        );
        BillRate saved = billRateRepository.save(billRate);
        return mapper.toBillRateResponse(saved);
    }

    @Override
    public void deleteBillRate(UUID billRateId) {
        BillRate billRate = findBillRate(billRateId);
        billRateRepository.delete(billRateId, billRate.getTenantId());
        log.info("Bill rate deleted: {}", billRateId);
    }

    private BillRate findBillRate(UUID billRateId) {
        UUID tenantId = getTenantId();
        return billRateRepository.findById(billRateId, tenantId)
                .orElseThrow(() -> new BusinessRuleException("Bill rate not found: " + billRateId));
    }

    private UUID getTenantId() {
        String tenantIdStr = TenantContextHolder.getTenantId();
        if (tenantIdStr == null) {
            throw new BusinessRuleException("Tenant ID not found in context");
        }
        return UUID.fromString(tenantIdStr);
    }

    private UUID getUserId() {
        UUID userId = TenantContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessRuleException("User ID not found in context");
        }
        return userId;
    }
}
