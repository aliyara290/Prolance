package com.dxc.billingservice.application.port.in;

import com.dxc.billingservice.application.dto.req.*;
import com.dxc.billingservice.application.dto.res.InvoiceResponse;
import com.dxc.billingservice.application.dto.res.InvoiceSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InvoiceUseCase {
    InvoiceResponse createInvoice(CreateInvoiceRequest request);
    InvoiceResponse getInvoice(UUID invoiceId);
    Page<InvoiceSummaryResponse> getAllInvoices(Pageable pageable);
    Page<InvoiceSummaryResponse> getInvoicesByProject(UUID projectId, Pageable pageable);
    Page<InvoiceSummaryResponse> getInvoicesByClient(UUID clientId, Pageable pageable);
    InvoiceResponse addLineItem(UUID invoiceId, AddLineItemRequest request);
    InvoiceResponse removeLineItem(UUID invoiceId, UUID lineItemId);
    InvoiceResponse updateLineItem(UUID invoiceId, UUID lineItemId, UpdateLineItemRequest request);
    InvoiceResponse updateInvoice(UUID invoiceId, UpdateInvoiceRequest request);
    InvoiceResponse sendInvoice(UUID invoiceId);
    InvoiceResponse markPaid(UUID invoiceId, String comment);
    InvoiceResponse markPartiallyPaid(UUID invoiceId, String comment);
    InvoiceResponse cancelInvoice(UUID invoiceId, String reason);
    InvoiceResponse generateAndAttachPdf(UUID invoiceId);
    void deleteInvoice(UUID invoiceId);
}
