package com.dxc.billingservice.infrastructure.adapter.out.pdf;

import com.dxc.billingservice.domain.model.aggregate.Invoice;
import com.dxc.billingservice.domain.model.entity.InvoiceLineItem;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class InvoicePdfGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(33, 37, 41));
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
    private static final Font BODY_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(33, 37, 41));
    private static final Font LABEL_FONT = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(108, 117, 125));
    private static final Font TOTAL_FONT = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(33, 37, 41));
    private static final Color PRIMARY_COLOR = new Color(13, 110, 253);
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);

    public byte[] generate(Invoice invoice) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            addHeader(document, invoice);
            document.add(new Paragraph(" "));
            addInvoiceDetails(document, invoice);
            document.add(new Paragraph(" "));
            addLineItemsTable(document, invoice);
            document.add(new Paragraph(" "));
            addTotalsSection(document, invoice);

            if (invoice.getNotes() != null && !invoice.getNotes().isBlank()) {
                document.add(new Paragraph(" "));
                addNotesSection(document, invoice);
            }

            document.close();
            log.info("PDF generated for invoice: {}", invoice.getInvoiceNumber());
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF for invoice: " + invoice.getInvoiceNumber(), e);
        }
    }

    private void addHeader(Document document, Invoice invoice) throws DocumentException {
        Paragraph title = new Paragraph("INVOICE", TITLE_FONT);
        title.setAlignment(Element.ALIGN_LEFT);
        document.add(title);

        Paragraph invoiceNum = new Paragraph(invoice.getInvoiceNumber(), new Font(Font.HELVETICA, 14, Font.NORMAL, PRIMARY_COLOR));
        invoiceNum.setAlignment(Element.ALIGN_LEFT);
        document.add(invoiceNum);
    }

    private void addInvoiceDetails(Document document, Invoice invoice) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 1});

        addDetailCell(table, "Issue Date", invoice.getIssueDate().format(DATE_FMT));
        addDetailCell(table, "Due Date", invoice.getDueDate().format(DATE_FMT));

        if (invoice.getPeriodStartDate() != null && invoice.getPeriodEndDate() != null) {
            addDetailCell(table, "Period", invoice.getPeriodStartDate().format(DATE_FMT) + " — " + invoice.getPeriodEndDate().format(DATE_FMT));
            addDetailCell(table, "Billing Type", invoice.getBillingType().name());
        }

        addDetailCell(table, "Status", invoice.getStatus().name());
        addDetailCell(table, "Currency", invoice.getCurrency());

        document.add(table);
    }

    private void addDetailCell(PdfPTable table, String label, String value) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPaddingBottom(5);

        Paragraph p = new Paragraph();
        p.add(new Chunk(label + ": ", LABEL_FONT));
        p.add(new Chunk(value, BODY_FONT));
        cell.addElement(p);
        table.addCell(cell);
    }

    private void addLineItemsTable(Document document, Invoice invoice) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.5f, 3f, 1f, 1f, 1.2f});

        addTableHeader(table, "#");
        addTableHeader(table, "Description");
        addTableHeader(table, "Quantity");
        addTableHeader(table, "Unit Price");
        addTableHeader(table, "Total");

        int index = 1;
        boolean alternate = false;
        for (InvoiceLineItem item : invoice.getLineItems()) {
            Color bgColor = alternate ? LIGHT_GRAY : Color.WHITE;

            addTableCell(table, String.valueOf(index++), bgColor, Element.ALIGN_CENTER);
            addTableCell(table, item.getDescription(), bgColor, Element.ALIGN_LEFT);
            addTableCell(table, item.getQuantity().toPlainString() + " " + item.getUnit().name().toLowerCase() + "(s)", bgColor, Element.ALIGN_CENTER);
            addTableCell(table, "€" + item.getUnitPrice().toPlainString(), bgColor, Element.ALIGN_RIGHT);
            addTableCell(table, "€" + item.getLineTotal().toPlainString(), bgColor, Element.ALIGN_RIGHT);

            alternate = !alternate;
        }

        document.add(table);
    }

    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(PRIMARY_COLOR);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderWidth(0);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Color bgColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BODY_FONT));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(new Color(222, 226, 230));
        table.addCell(cell);
    }

    private void addTotalsSection(Document document, Invoice invoice) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(40);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);

        addTotalRow(table, "Subtotal (HT)", "€" + invoice.getSubtotal().toPlainString(), BODY_FONT);
        addTotalRow(table, "TVA (" + invoice.getTaxRate().toPlainString() + "%)", "€" + invoice.getTaxAmount().toPlainString(), BODY_FONT);

        PdfPCell labelCell = new PdfPCell(new Phrase("Total (TTC)", TOTAL_FONT));
        labelCell.setBorder(PdfPCell.TOP);
        labelCell.setBorderColor(PRIMARY_COLOR);
        labelCell.setBorderWidth(2);
        labelCell.setPadding(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase("€" + invoice.getTotalAmount().toPlainString(), TOTAL_FONT));
        valueCell.setBorder(PdfPCell.TOP);
        valueCell.setBorderColor(PRIMARY_COLOR);
        valueCell.setBorderWidth(2);
        valueCell.setPadding(8);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);

        document.add(table);
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setPadding(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private void addNotesSection(Document document, Invoice invoice) throws DocumentException {
        Paragraph notesLabel = new Paragraph("Notes:", LABEL_FONT);
        document.add(notesLabel);

        Paragraph notesText = new Paragraph(invoice.getNotes(), BODY_FONT);
        notesText.setSpacingBefore(5);
        document.add(notesText);
    }
}
