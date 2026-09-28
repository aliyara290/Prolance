package com.dxc.billingservice.application.service;

import com.dxc.billingservice.application.dto.req.GenerateInvoiceRequest;
import com.dxc.billingservice.application.dto.res.InvoiceResponse;
import com.dxc.billingservice.application.mapper.BillingMapper;
import com.dxc.billingservice.application.port.in.InvoiceGenerationUseCase;
import com.dxc.billingservice.application.port.out.BillRateRepository;
import com.dxc.billingservice.application.port.out.InvoiceRepository;
import com.dxc.billingservice.application.port.in.TimeTrackingUseCase;
import com.dxc.billingservice.application.dto.res.TimeEntryResponse;
import com.dxc.billingservice.domain.exception.BusinessRuleException;
import com.dxc.billingservice.domain.model.aggregate.Invoice;
import com.dxc.billingservice.domain.model.entity.BillRate;
import com.dxc.billingservice.domain.model.valueobject.BillingType;
import com.dxc.billingservice.domain.model.valueobject.LineItemUnit;
import com.dxc.billingservice.application.port.out.feign.AttachmentFeignPort;
import com.dxc.billingservice.infrastructure.adapter.out.pdf.InvoicePdfGenerator;
import com.dxc.billingservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceGenerationService implements InvoiceGenerationUseCase {

    private final InvoiceRepository invoiceRepository;
    private final BillRateRepository billRateRepository;
    private final TimeTrackingUseCase timeTrackingUseCase;
    private final BillingMapper mapper;
    private final InvoicePdfGenerator invoicePdfGenerator;
    private final AttachmentFeignPort attachmentFeignPort;

    @Override
    public InvoiceResponse generateFromTimeEntries(GenerateInvoiceRequest request) {
        UUID tenantId = getTenantId();
        UUID userId = getUserId();

        List<TimeEntryResponse> timeEntries = timeTrackingUseCase.getProjectTimeEntries(
                request.projectId(), request.periodStartDate().atStartOfDay(), request.periodEndDate().atTime(23, 59, 59));

        if (timeEntries.isEmpty()) {
            throw new BusinessRuleException("No time entries found for the given period");
        }

        String invoiceNumber = generateInvoiceNumber(tenantId);

        Invoice invoice = Invoice.create(
                tenantId,
                request.projectId(),
                request.clientId(),
                invoiceNumber,
                BillingType.HOURLY,
                LocalDate.now(),
                request.dueDate(),
                request.periodStartDate(),
                request.periodEndDate(),
                request.taxRate(),
                request.notes(),
                userId
        );

        Map<UUID, BigDecimal> hoursByUser = timeEntries.stream()
                .collect(Collectors.groupingBy(
                        TimeEntryResponse::userId,
                        Collectors.reducing(BigDecimal.ZERO, entry -> BigDecimal.valueOf(entry.durationMinutes()).divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP), BigDecimal::add)
                ));

        int order = 1;
        for (Map.Entry<UUID, BigDecimal> entry : hoursByUser.entrySet()) {
            UUID employeeId = entry.getKey();
            BigDecimal totalHours = entry.getValue();

            BillRate rate = billRateRepository.findByProjectIdAndUserId(request.projectId(), employeeId, tenantId)
                    .orElseThrow(() -> new BusinessRuleException("No bill rate found for user " + employeeId));

            String description = rate.getSeniorityLevel() + " (" + rate.getEducationLevel().name().replace("_", "+").replace("BAC+", "Bac+") + ") — " + totalHours + " hours";

            invoice.addLineItem(employeeId, description, totalHours, LineItemUnit.HOUR, rate.getHourlyRate(), order++);
        }

        Invoice saved = invoiceRepository.save(invoice);
        
        try {
            // Generate PDF
            byte[] pdfBytes = invoicePdfGenerator.generate(saved);
            String fileName = saved.getInvoiceNumber() + ".pdf";
            
            // Upload to Attachment Service
            AttachmentFeignPort.AttachmentResponseDTO attachment = attachmentFeignPort.uploadFile(
                    pdfBytes,
                    fileName,
                    "application/pdf",
                    "INVOICE",
                    saved.getId()
            );
            
            // Update Invoice with attachment ID
            saved.attachDocument(attachment.id());
            saved = invoiceRepository.save(saved);
            log.info("PDF generated and uploaded successfully for invoice: {}", saved.getInvoiceNumber());
        } catch (Exception e) {
            log.error("Failed to generate or upload PDF for invoice: {}", saved.getInvoiceNumber(), e);
            // We don't fail the transaction, the invoice is still created
        }

        log.info("Invoice auto-generated: {} with {} line items", invoiceNumber, hoursByUser.size());
        return mapper.toInvoiceResponse(saved);
    }

    private String generateInvoiceNumber(UUID tenantId) {
        int year = LocalDate.now().getYear();
        String prefix = "INV-" + year + "-";
        int count = invoiceRepository.countByTenantIdAndInvoiceNumberStartingWith(tenantId, prefix);
        return prefix + String.format("%04d", count + 1);
    }

    private UUID getTenantId() {
        String tenantIdStr = TenantContextHolder.getTenantId();
        if (tenantIdStr == null) throw new BusinessRuleException("Tenant ID not found in context");
        return UUID.fromString(tenantIdStr);
    }

    private UUID getUserId() {
        UUID userId = TenantContextHolder.getUserId();
        if (userId == null) throw new BusinessRuleException("User ID not found in context");
        return userId;
    }
}
