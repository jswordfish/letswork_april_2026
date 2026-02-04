package com.letswork.crm.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.letswork.crm.entities.Contract;
import com.letswork.crm.entities.LetsWorkClient;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;



@Service
public class ContractDocumentService {

    @Autowired
    private TemplateEngine templateEngine;

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
        context.setVariable("officeHours", contract.getOfficeHours());
        context.setVariable("gstNumber", contract.getGstNumber());
        context.setVariable("billingCycle", contract.getBillingCycle());
        context.setVariable("advanceToken", contract.getAdvanceTokenAmount());
        context.setVariable("commencementDate", contract.getCommencementDate());
        context.setVariable("amenities", contract.getAmenitiesIncluded());

        String html = templateEngine.process("contract-agreement", context);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(os);
        try {
			builder.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return os.toByteArray();
    }
}
