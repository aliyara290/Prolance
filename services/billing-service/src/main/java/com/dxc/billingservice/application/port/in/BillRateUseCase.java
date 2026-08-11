package com.dxc.billingservice.application.port.in;

import com.dxc.billingservice.application.dto.req.CreateBillRateRequest;
import com.dxc.billingservice.application.dto.req.UpdateBillRateRequest;
import com.dxc.billingservice.application.dto.res.BillRateResponse;

import java.util.List;
import java.util.UUID;

public interface BillRateUseCase {
    BillRateResponse createBillRate(CreateBillRateRequest request);
    BillRateResponse getBillRate(UUID billRateId);
    List<BillRateResponse> getBillRatesByProject(UUID projectId);
    BillRateResponse getBillRateByProjectAndUser(UUID projectId, UUID userId);
    BillRateResponse updateBillRate(UUID billRateId, UpdateBillRateRequest request);
    void deleteBillRate(UUID billRateId);
}
