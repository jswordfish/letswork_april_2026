package com.letswork.crm.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        context.setVariable("feesPerMonth", contract.getFeesPerMonth());
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

        // 🔥 Parse amenities JSON
        List<AmenityDto> amenities = new ArrayList<>();
        try {
            if (contract.getAmenitiesIncluded() != null && !contract.getAmenitiesIncluded().isEmpty()) {
                amenities = objectMapper.readValue(
                        contract.getAmenitiesIncluded(),
                        new TypeReference<List<AmenityDto>>() {}
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid amenities JSON format", e);
        }

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
}
