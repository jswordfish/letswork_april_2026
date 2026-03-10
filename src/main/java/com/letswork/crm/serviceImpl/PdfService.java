package com.letswork.crm.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.letswork.crm.entities.Invoice;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfService {
	
	public byte[] generateInvoicePdf(String html) {

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();

            return os.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }
	
	public String buildInvoiceHtml(Invoice invoice) {

        try {

            String html = new String(
                    Files.readAllBytes(
                            Paths.get("src/main/resources/templates/invoice-template.html")
                    )
            );

            html = html.replace("${invoiceNumber}", String.valueOf(invoice.getId()));
            html = html.replace("${invoiceDate}", LocalDate.now().toString());
            html = html.replace("${amount}", String.valueOf(invoice.getAmount()));

            html = html.replace("${cgst}", String.valueOf(invoice.getAmount()*0.09));
            html = html.replace("${sgst}", String.valueOf(invoice.getAmount()*0.09));
            html = html.replace("${total}", String.valueOf(invoice.getAmount()*1.18));

            html = html.replace("${serviceDescription}", invoice.getBookingType().name());
            html = html.replace("${quantity}", "1");
            html = html.replace("${rate}", String.valueOf(invoice.getAmount()));

            return html;

        } catch (Exception e) {

            throw new RuntimeException("Failed to build invoice template", e);

        }
    }

}
