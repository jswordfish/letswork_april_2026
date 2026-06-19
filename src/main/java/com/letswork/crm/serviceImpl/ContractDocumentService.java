 package com.letswork.crm.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.dtos.AgreementDto;
import com.letswork.crm.dtos.AmenityDto;
import com.letswork.crm.entities.Contract;
import com.letswork.crm.entities.LetsWorkClient;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class ContractDocumentService {

    @Autowired
    private TemplateEngine templateEngine;
    
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    

    public byte[] generateAgreementPdf(Contract contract) {

        Context context = new Context();
        LetsWorkClient client = contract.getLetsWorkClient();

        context.setVariable("clientName", client.getClientCompanyName());
        context.setVariable("clientAddress", contract.getClientAdress());
        context.setVariable("usageOfPremises", contract.getUsageOfPremises());
        context.setVariable("agreementTenure", contract.getAgreementTenureInMonths() + " Months");
        context.setVariable("lockInPeriod", contract.getLockInPeriodInMonths() + " Months");
        context.setVariable("noticePeriod", contract.getNoticePeriodInMonths() + " Months");
        context.setVariable("cabinOffered", contract.getCabinOffered());
        context.setVariable("workstation", contract.getWorkstation());
//        context.setVariable("feesPerMonth", contract.getFeesPerMonth());
        context.setVariable("depositAmount", contract.getDepositAmountInRupees());
        context.setVariable("escalation", contract.getEscalationInPercentage() + "%");
        context.setVariable("officeHoursStart", contract.getOfficeHoursStart());
        context.setVariable("officeHoursEnd", contract.getOfficeHoursEnd());
        context.setVariable("officeHoursStartSat", contract.getOfficeHoursStartSat());
        context.setVariable("officeHoursEndSat", contract.getOfficeHoursEndSat());
        context.setVariable("gstNumber", contract.getGstNumber());
        context.setVariable("billingCycle", contract.getBillingCycle());
        context.setVariable("advanceToken", contract.getAdvanceTokenAmount());
        context.setVariable("commencementDate", contract.getCommencementDate());

        // 🔥 Normalize + parse amenities
        List<AmenityDto> amenities = parseAmenities(contract.getAmenitiesIncluded());

        context.setVariable("amenities", amenities);

        String html = templateEngine.process("contract-agreement", context);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(os);

        try {
            builder.run();
        } catch (IOException e) {
            throw new RuntimeException("PDF generation failed", e);
        }

        return os.toByteArray();
    }
    
    public byte[] generateAgreementPdfFromDto(AgreementDto dto) {

        Context context = new Context();
        

        context.setVariable("clientName", dto.getLeadCompanyName());
        context.setVariable("clientAddress", dto.getClientAdress());
        context.setVariable("usageOfPremises", dto.getUsageOfPremises());
        context.setVariable("agreementTenure", dto.getAgreementTenureInMonths() + " Months");
        context.setVariable("lockInPeriod", dto.getLockInPeriodInMonths() + " Months");
        context.setVariable("noticePeriod", dto.getNoticePeriodInMonths() + " Months");
        context.setVariable("cabinOffered", dto.getCabinOffered());
        context.setVariable("workstation", dto.getWorkstation());
        context.setVariable("feesPerMonth", dto.getFeesPerMonth());
        context.setVariable("depositAmount", dto.getDepositAmountInRupees());
        context.setVariable("escalation", dto.getEscalationInPercentage() + "%");
        context.setVariable("officeHoursStart", dto.getOfficeHoursStart());
        context.setVariable("officeHoursEnd", dto.getOfficeHoursEnd());
        context.setVariable("officeHoursStartSat", dto.getOfficeHoursStartSat());
        context.setVariable("officeHoursEndSat", dto.getOfficeHoursEndSat());
        context.setVariable("gstNumber", dto.getGstNumber());
        context.setVariable("billingCycle", dto.getBillingCycle());
        context.setVariable("advanceToken", dto.getAdvanceTokenAmount());
        context.setVariable("commencementDate", dto.getCommencementDate());

        // 🔥 Normalize + parse amenities
        List<AmenityDto> amenities = parseAmenities(dto.getAmenitiesIncluded());

        context.setVariable("amenities", amenities);

        String html = templateEngine.process("contract-agreement", context);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(os);

        try {
            builder.run();
        } catch (IOException e) {
            throw new RuntimeException("PDF generation failed", e);
        }

        return os.toByteArray();
    }
    
    private List<AmenityDto> parseAmenities(String rawAmenities) {

        if (rawAmenities == null || rawAmenities.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            // STEP 1: Remove line breaks & trim
            String cleaned = rawAmenities
                    .replace("\n", "")
                    .replace("\r", "")
                    .trim();

            // STEP 2: Parse into List
            List<AmenityDto> list = objectMapper.readValue(
                    cleaned,
                    new TypeReference<List<AmenityDto>>() {}
            );

            // STEP 3: Convert back to DB-safe single line JSON
            String normalizedJson = objectMapper.writeValueAsString(list);

            // STEP 4: Store normalized JSON back in entity (optional but recommended)
            // so DB always has clean format
            // ⚠ only if you want auto-fix behaviour
            // contract.setAmenitiesIncluded(normalizedJson);

            return list;

        } catch (Exception e) {
            throw new RuntimeException("Invalid amenities JSON format", e);
        }
    }
    
}
