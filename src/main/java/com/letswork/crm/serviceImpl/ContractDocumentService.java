 package com.letswork.crm.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    
    public static String formatWithDaySuffix(TemporalAccessor date) {
	    if (date == null) {
	        return "";
	    }
	    
	    int day = date.get(ChronoField.DAY_OF_MONTH);
	    String suffix = getDayOfMonthSuffix(day);
	    
	    // Pattern: "Mon, 20" + "th" + " Jul 2026"
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, d'" + suffix + "' MMM yyyy");
	    return formatter.format(date);
	}

	private static String getDayOfMonthSuffix(int day) {
	    if (day >= 11 && day <= 13) {
	        return "th";
	    }
	    switch (day % 10) {
	        case 1:  return "st";
	        case 2:  return "nd";
	        case 3:  return "rd";
	        default: return "th";
	    }
	}
    

    public byte[] generateAgreementPdf(Contract contract) {

        Context context = new Context();
        LetsWorkClient client = contract.getLetsWorkClient();
        
        context.setVariable("agreementDate", contract.getCreateDate());
        context.setVariable("clientPan", contract.getPanNumber());

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
        
        context.setVariable("gstNumber", contract.getGstNumber());
        context.setVariable("billingCycle", contract.getBillingCycle());
        context.setVariable("advanceToken", contract.getAdvanceTokenAmount());
        context.setVariable("handingOver", contract.getHandOver());
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
        
        context.setVariable("agreementDate", dto.getCreateDate());
        String panNumber = Optional.ofNullable(dto)
                
                .map(c -> c.getPanNumber())
                .orElse("");

        context.setVariable("clientPan", panNumber);

        context.setVariable("clientName", dto.getLeadCompanyName());
        context.setVariable("clientAddress", dto.getClientAdress());
        context.setVariable("usageOfPremises", dto.getUsageOfPremises());
        context.setVariable("agreementTenure", dto.getAgreementTenureInMonths() + " Months");
        context.setVariable("lockInPeriod", dto.getLockInPeriodInMonths() + " Months");
        context.setVariable("noticePeriod", dto.getNoticePeriodInMonths() + " Months");
        context.setVariable("cabinOffered", dto.getCabinOffered());
        context.setVariable("workstation", dto.getWorkstation());
        String formattedFees = String.format("%.2f", dto.getFeesPerMonth());
        context.setVariable("feesPerMonth", formattedFees);
        context.setVariable("depositAmount", dto.getDepositAmountInRupees()+".00");
        context.setVariable("escalation", dto.getEscalationInPercentage() + "%");
        context.setVariable("gstNumber", dto.getGstNumber());
        context.setVariable("carParking", dto.getCarParkingDetails());
        Integer rawSetupCharge = dto.getOneTimeSetupCharge();

	    // Cast to double so it can format with 2 decimal places
	    String formattedSetupCharge = (rawSetupCharge != null) ? String.format("%.2f", (double) rawSetupCharge) : "0.00";
	
	    context.setVariable("setupCharge", formattedSetupCharge);
        context.setVariable("billingCycle", dto.getBillingCycle());
        Integer rawToken = dto.getAdvanceTokenAmount();

	    // Cast to double so String.format can apply the decimal places
	    String formattedToken = (rawToken != null) ? String.format("%.2f", (double) rawToken) : "0.00";
	
	    context.setVariable("advanceToken", formattedToken);
        context.setVariable("handingOver", dto.getHandOver());
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
