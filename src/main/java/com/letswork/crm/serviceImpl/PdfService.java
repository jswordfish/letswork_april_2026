package com.letswork.crm.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.Booking;
import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.entities.DayPassBookingDirect;
import com.letswork.crm.entities.DayPassBundleBooking;
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.entities.LetsWorkClient;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfService {
	
	DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
	
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
	        Booking booking = invoice.getBooking();

	        ClassPathResource resource = null;
	        if (booking instanceof ConferenceBookingDirect) {
	        	    resource =
	  	                new ClassPathResource("templates/invoice-conference-booking-direct.html");
	        }
	      	    else if (booking instanceof ConferenceBundleBooking) {
	      	    	 resource =
	 	  	                new ClassPathResource("templates/invoice-conference-bundle-booking.html");
	      	    }
	      	  else if (booking instanceof DayPassBookingDirect) {
	      	    	 resource =
	 	  	                new ClassPathResource("templates/invoice-day-pass-booking-direct.html");
	      	    }
	      	else if (booking instanceof DayPassBundleBooking) {
     	    	 resource =
	  	                new ClassPathResource("templates/invoice-day-pass-bundle-booking.html");
     	    }

	        String html = new String(
	                resource.getInputStream().readAllBytes(),
	                StandardCharsets.UTF_8
	        );

	        // ================= BASIC =================
	        html = html.replace("${invoiceNumber}", String.valueOf(invoice.getId()));
	        html = html.replace("${invoiceDate}", LocalDate.now().toString());
	        BigDecimal invoiceAmount = BigDecimal.valueOf(booking.getFrontendAmount());
	        html = html.replace("${amount}", invoiceAmount.toPlainString());

	        // ================= CUSTOMER =================
	        LetsWorkClient client = booking.getLetsWorkClient();

	        html = html.replace("${customerName}", client.getClientCompanyName());
//	        html = html.replace("${customerAddress}", 
//	                Optional.ofNullable(client.getLetsWorkCentre()).orElse("N/A"));
//	        html = html.replace("${customerGstin}", 
//	                Optional.ofNullable(client.getGstNumber())).orElse("N/A"));

	        // ================= LINE ITEMS =================
	        String lineItems = buildLineItems(booking);

	        html = html.replace("${lineItems}", lineItems);

	     // ================= FRONTEND VALUES =================
	        BigDecimal originalAmount = BigDecimal.valueOf(booking.getFrontendAmount());

	        Integer discountPercent = booking.getFrontendDiscountPercentage() != null
	                ? booking.getFrontendDiscountPercentage()
	                : 0;

	        BigDecimal discountedAmount = BigDecimal.valueOf(booking.getFrontendDiscountedAmount());

	        // ================= TAX =================
//	        BigDecimal taxRate = new BigDecimal("0.09");

	        float discountedAmount1 = invoice.getBooking().getFrontendDiscountedAmount();

	        Integer cgstPercent = invoice.getBooking().getFrontendCgstPercentage();
	        Integer sgstPercent = invoice.getBooking().getFrontendSgstPercentage();

	        float cgstAmount = discountedAmount1 * cgstPercent / 100f;
	        float sgstAmount = discountedAmount1 * sgstPercent / 100f;

	        float total = invoice.getBooking().getFrontendFinalAmountAfterAddingTax();
	                

	        // ================= HTML REPLACEMENTS =================
	        html = html.replace("${bookingRef}", booking.getReferenceId());

	        if (booking instanceof DayPassBundleBooking || booking instanceof ConferenceBundleBooking) {
	            String dt = booking.getStartDate() != null
	                    ? booking.getStartDate().toString()
	                    : booking.getDateOfPurchase().toLocalDate().toString();
	            html = html.replace("${bookingDate}", dt);
	        } else {
	            html = html.replace("${bookingDate}", booking.getStartDate().toString());
	        }

	        // NEW FIELDS
	        html = html.replace("${originalAmount}", originalAmount.toPlainString());
	        html = html.replace("${discountPercent}", String.valueOf(discountPercent));
	        html = html.replace("${discountedAmount}", discountedAmount.toPlainString());

	        // TAX
	        
	        html = html.replace("${cgstPercent}", String.valueOf(cgstPercent));
	        html = html.replace("${sgstPercent}", String.valueOf(sgstPercent));
	        
	        html = html.replace("${cgst}", String.valueOf(cgstAmount));
	        html = html.replace("${sgst}", String.valueOf(sgstAmount));
	        html = html.replace("${total}", String.valueOf(total));

	        return html;

	    } catch (Exception e) {
	        throw new RuntimeException("Failed to build invoice template", e);
	    }
	}
	
	private String buildLineItems(Booking booking) {

	    StringBuilder rows = new StringBuilder();
	    int index = 1;
	    // 🔵 Conference Direct
	    if (booking instanceof ConferenceBookingDirect) {

	        ConferenceBookingDirect conf = (ConferenceBookingDirect) booking;
	        for (ConferenceRoomTimeSlot slot : conf.getSlots()) {


	        	  rows.append(buildRowConferenceBookingDirect(
		                    index++,
		                    "Conference Room Booking (" + slot.getStartTime() + " - " + slot.getEndTime() + ")",
		                    conf.getConferenceRoom().getName(),
		                    conf.getLetsWorkCentre().getName(),
		                    conf.getAppliedOffer() == null?"NA": conf.getAppliedOffer().getName()
		                   

		            ));
	        }
	    }
	    // 🟢 Conference Bundle (FULL PURCHASE - NOT USAGE)
	    else if (booking instanceof ConferenceBundleBooking) {
	        ConferenceBundleBooking bundle = (ConferenceBundleBooking) booking;
	        String dateOfPurchase = null;
	        	if(bundle.getStartDate() != null) {
	        		dateOfPurchase = bundle.getStartDate().toString();
	        	}
	        	else if(bundle.getDateOfPurchase() != null) {
	        		dateOfPurchase = bundle.getDateOfPurchase().toLocalDate().toString();
	        	}
	        	else {
	        		dateOfPurchase = "NA";
	        	}
	        rows.append(buildRowConferenceBundleBooking(
	                index++,
	                "Conference Room Bundle Booking",
	                bundle.getConferenceBundle().getId(),
	                dateOfPurchase,
	                bundle.getRemainingHours(),
	                bundle.getExpiryDate().toString()
	        ));
	    }
	    // 🟡 Day Pass Direct
	    else if (booking instanceof DayPassBookingDirect) {
	    	String dateOfPurchase = null;
        	if(booking.getStartDate() != null) {
        		dateOfPurchase = booking.getStartDate().toString();
        	}
        	else if(booking.getDateOfPurchase() != null) {
        		dateOfPurchase = booking.getDateOfPurchase().toLocalDate().toString();
        	}
        	else {
        		dateOfPurchase = "NA";
        	}
	        DayPassBookingDirect dp = (DayPassBookingDirect) booking;

	        rows.append(buildRowDayPassBookingDirect(
	                index++,
	                "Day pass Booking Direct",
	                dp.getNumberOfPasses(),
	                dateOfPurchase,
	                dp.getAppliedOffer()==null?"NA": dp.getAppliedOffer().getName(),
	                dp.getPreviousBookingId()
	        ));
	    }

	    // 🟣 Day Pass Bundle (FULL PURCHASE - NOT USAGE)
	    else if (booking instanceof DayPassBundleBooking) {
	        DayPassBundleBooking dpb = (DayPassBundleBooking) booking;
	        rows.append(buildRowDayPassBundleBooking(
	                index++,
	                "Day Pass Bundle Purchase",
	                dpb.getExpiryDate().toString(),
	                dpb.getRemainingNumberOfDays(),
	                dpb.getAppliedOffer() ==null?"NA": dpb.getAppliedOffer().getName()
	        ));
	    }

	    return rows.toString();
	}
	
	private String buildRow(
	        int index,
	        String description,
	        String hsn,
	        String qty,
	        String rate,
	        String amount
	) {

	    return "<tr>" +
	            "<td>" + index + "</td>" +
	            "<td>" + description + "</td>" +
	            "<td>" + hsn + "</td>" +
	            "<td>" + qty + "</td>" +
	            "<td>" + rate + "</td>" +
	            "<td>" + amount + "</td>" +
	            "</tr>";
	}
	
	private String buildRowConferenceBookingDirect(
	        int index,
	        String description,
	        String name,
	        String center,
	        String appliedOffer
	       
	        
	) {
        
	    return "<tr>" +
	            "<td>" + index + "</td>" +
	            "<td>" + description + "</td>" +
	            "<td>" + name + "</td>" +
	            "<td>" + center + "</td>" +
	            "<td>" + appliedOffer + "</td>" +
	            "</tr>";
	}
	
	private String buildRowConferenceBundleBooking(
	        int index,
	        String description,
	        Long ConferenceBundleId,
	        String dateOfPurchase,
	        Float remainingHours,
	        String expiryDate
	) {
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        //String expiryDate2 = expiryDate.format(formatter);
        
	    return "<tr>" +
	            "<td>" + index + "</td>" +
	            "<td>" + description + "</td>" +
	            "<td>" + ConferenceBundleId + "</td>" +
	            "<td>" + dateOfPurchase + "</td>" +
	            "<td>" + remainingHours + "</td>" +
	            "<td>" + expiryDate + "</td>" +
	            "</tr>";
	}
	
	
	private String buildRowDayPassBookingDirect(
	        int index,
	        String description,
	        Integer numberOfPasses,
	        String startDate,
	        String appliedOffer ,
	        Long previousBookingId
	        
	) {
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
       // String expiryDate2 = startDate.format(formatter);
        
	    return "<tr>" +
	            "<td>" + index + "</td>" +
	            "<td>" + description + "</td>" +
	            "<td>" + numberOfPasses + "</td>" +
	            "<td>" + startDate + "</td>" +
	            "<td>" + appliedOffer + "</td>" +
	            "<td>" + previousBookingId + "</td>" +
	            "</tr>";
	}
	
	private String buildRowDayPassBundleBooking(
	        int index,
	        String description,
	        String expiryDate,
	        Integer remainingNumberOfDays,
	        String Offer 
	        
	) {
	    return "<tr>" +
	            "<td>" + index + "</td>" +
	            "<td>" + description + "</td>" +
//	            "<td>" + hsn + "</td>" +
	            "<td>" + expiryDate + "</td>" +
	            "<td>" + remainingNumberOfDays + "</td>" +
	            "<td>" + Offer + "</td>" +
	            "</tr>";
	}

}
