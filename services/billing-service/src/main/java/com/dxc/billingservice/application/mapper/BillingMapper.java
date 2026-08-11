package com.dxc.billingservice.application.mapper;

import com.dxc.billingservice.application.dto.res.BillRateResponse;
import com.dxc.billingservice.application.dto.res.InvoiceLineItemResponse;
import com.dxc.billingservice.application.dto.res.InvoiceResponse;
import com.dxc.billingservice.application.dto.res.InvoiceSummaryResponse;
import com.dxc.billingservice.application.dto.res.TimeEntryResponse;
import com.dxc.billingservice.domain.model.aggregate.Invoice;
import com.dxc.billingservice.domain.model.entity.BillRate;
import com.dxc.billingservice.domain.model.entity.InvoiceLineItem;
import com.dxc.billingservice.domain.model.entity.TimeEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BillingMapper {

    InvoiceLineItemResponse toLineItemResponse(InvoiceLineItem lineItem);

    List<InvoiceLineItemResponse> toLineItemResponses(List<InvoiceLineItem> lineItems);

    @Mapping(target = "lineItems", source = "lineItems")
    InvoiceResponse toInvoiceResponse(Invoice invoice);

    InvoiceSummaryResponse toInvoiceSummaryResponse(Invoice invoice);

    BillRateResponse toBillRateResponse(BillRate billRate);

    List<BillRateResponse> toBillRateResponses(List<BillRate> billRates);

    TimeEntryResponse toTimeEntryResponse(TimeEntry timeEntry);
}
