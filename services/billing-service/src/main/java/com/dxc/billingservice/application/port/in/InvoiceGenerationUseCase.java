package com.dxc.billingservice.application.port.in;

import com.dxc.billingservice.application.dto.req.GenerateInvoiceRequest;
import com.dxc.billingservice.application.dto.res.InvoiceResponse;

public interface InvoiceGenerationUseCase {
    InvoiceResponse generateFromTimeEntries(GenerateInvoiceRequest request);
}
