package com.dxc.billingservice.application.service;

import com.dxc.billingservice.application.dto.req.*;
import com.dxc.billingservice.application.dto.res.InvoiceResponse;
import com.dxc.billingservice.application.dto.res.InvoiceSummaryResponse;
import com.dxc.billingservice.application.mapper.BillingMapper;
import com.dxc.billingservice.application.port.in.InvoiceUseCase;
import com.dxc.billingservice.application.port.out.InvoiceRepository;
import com.dxc.billingservice.application.port.out.feign.AttachmentFeignPort;
import com.dxc.billingservice.domain.exception.BusinessRuleException;
import com.dxc.billingservice.domain.model.aggregate.Invoice;
import com.dxc.billingservice.domain.model.valueobject.BillingType;
import com.dxc.billingservice.infrastructure.adapter.out.pdf.InvoicePdfGenerator;
import com.dxc.billingservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService implements InvoiceUseCase {

    private final InvoiceRepository invoiceRepository;
    private final BillingMapper mapper;
    private final InvoicePdfGenerator pdfGenerator;
    private final AttachmentFeignPort attachmentFeignPort;

    @Override
    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        UUID tenantId = getTenantId();
        UUID userId = getUserId();

        String invoiceNumber = generateInvoiceNumber(tenantId);

        Invoice invoice = Invoice.create(
                tenantId,
                request.projectId(),
                request.clientId(),
                invoiceNumber,
                request.billingType(),
                request.issueDate(),
                request.dueDate(),
                request.periodStartDate(),
                request.periodEndDate(),
                request.taxRate(),
                request.notes(),
                userId
        );

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice created: {} for project: {}", invoiceNumber, request.projectId());
        return mapper.toInvoiceResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(UUID invoiceId) {
        Invoice invoice = findInvoice(invoiceId);
        return mapper.toInvoiceResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceSummaryResponse> getAllInvoices(Pageable pageable) {
        UUID tenantId = getTenantId();
        return invoiceRepository.findAll(tenantId, pageable).map(mapper::toInvoiceSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceSummaryResponse> getInvoicesByProject(UUID projectId, Pageable pageable) {
        UUID tenantId = getTenantId();
        return invoiceRepository.findByProjectId(projectId, tenantId, pageable).map(mapper::toInvoiceSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceSummaryResponse> getInvoicesByClient(UUID clientId, Pageable pageable) {
        UUID tenantId = getTenantId();
        return invoiceRepository.findByClientId(clientId, tenantId, pageable).map(mapper::toInvoiceSummaryResponse);
    }

    @Override
    public InvoiceResponse addLineItem(UUID invoiceId, AddLineItemRequest request) {
        Invoice invoice = findInvoice(invoiceId);
        invoice.addLineItem(
                request.userId(),
                request.description(),
                request.quantity(),
                request.unit(),
                request.unitPrice(),
                request.displayOrder()
        );
        Invoice saved = invoiceRepository.save(invoice);
        return mapper.toInvoiceResponse(saved);
    }

    @Override
    public InvoiceResponse removeLineItem(UUID invoiceId, UUID lineItemId) {
        Invoice invoice = findInvoice(invoiceId);
        invoice.removeLineItem(lineItemId);
        Invoice saved = invoiceRepository.save(invoice);
        return mapper.toInvoiceResponse(saved);
    }

    @Override
    public InvoiceResponse updateLineItem(UUID invoiceId, UUID lineItemId, UpdateLineItemRequest request) {
        Invoice invoice = findInvoice(invoiceId);
        invoice.updateLineItem(
                lineItemId,
                request.description(),
                request.quantity(),
                request.unit(),
                request.unitPrice(),
                request.displayOrder()
        );
        Invoice saved = invoiceRepository.save(invoice);
        return mapper.toInvoiceResponse(saved);
    }

    @Override
    public InvoiceResponse updateInvoice(UUID invoiceId, UpdateInvoiceRequest request) {
        Invoice invoice = findInvoice(invoiceId);
        invoice.updateDetails(
                request.issueDate(),
                request.dueDate(),
                request.periodStartDate(),
                request.periodEndDate(),
                request.taxRate(),
                request.notes()
        );
        Invoice saved = invoiceRepository.save(invoice);
        return mapper.toInvoiceResponse(saved);
    }

    @Override
    public InvoiceResponse sendInvoice(UUID invoiceId) {
        Invoice invoice = findInvoice(invoiceId);
        invoice.send(getUserId());
        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice sent: {}", invoice.getInvoiceNumber());
        return mapper.toInvoiceResponse(saved);
    }

    @Override
    public InvoiceResponse markPaid(UUID invoiceId, String comment) {
        Invoice invoice = findInvoice(invoiceId);
        invoice.markPaid(getUserId(), comment);
        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice paid: {}", invoice.getInvoiceNumber());
        return mapper.toInvoiceResponse(saved);
    }

    @Override
    public InvoiceResponse markPartiallyPaid(UUID invoiceId, String comment) {
        Invoice invoice = findInvoice(invoiceId);
        invoice.markPartiallyPaid(getUserId(), comment);
        Invoice saved = invoiceRepository.save(invoice);
        return mapper.toInvoiceResponse(saved);
    }

    @Override
    public InvoiceResponse cancelInvoice(UUID invoiceId, String reason) {
        Invoice invoice = findInvoice(invoiceId);
        invoice.cancel(getUserId(), reason);
        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice cancelled: {}", invoice.getInvoiceNumber());
        return mapper.toInvoiceResponse(saved);
    }

    @Override
    public InvoiceResponse generateAndAttachPdf(UUID invoiceId) {
        Invoice invoice = findInvoice(invoiceId);

        byte[] pdfBytes = pdfGenerator.generate(invoice);
        String fileName = invoice.getInvoiceNumber() + ".pdf";

        AttachmentFeignPort.AttachmentResponseDTO attachment = attachmentFeignPort.uploadFile(
                pdfBytes, fileName, "application/pdf", "INVOICE", invoice.getId());

        invoice.attachPdf(attachment.id());
        Invoice saved = invoiceRepository.save(invoice);
        log.info("PDF generated and attached for invoice: {}", invoice.getInvoiceNumber());
        return mapper.toInvoiceResponse(saved);
    }

    @Override
    public void deleteInvoice(UUID invoiceId) {
        Invoice invoice = findInvoice(invoiceId);
        invoiceRepository.delete(invoiceId, invoice.getTenantId());
        log.info("Invoice deleted: {}", invoice.getInvoiceNumber());
    }

    private Invoice findInvoice(UUID invoiceId) {
        UUID tenantId = getTenantId();
        return invoiceRepository.findById(invoiceId, tenantId)
                .orElseThrow(() -> new BusinessRuleException("Invoice not found: " + invoiceId));
    }

    private String generateInvoiceNumber(UUID tenantId) {
        int year = LocalDate.now().getYear();
        String prefix = "INV-" + year + "-";
        int count = invoiceRepository.countByTenantIdAndInvoiceNumberStartingWith(tenantId, prefix);
        return prefix + String.format("%04d", count + 1);
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
