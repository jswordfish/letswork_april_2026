package com.letswork.crm.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
	
	public void generatePdf(String html, OutputStream os) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.useFastMode();

            // ✅ Load font properly
            builder.useFont(() ->
                    getClass()
                            .getResourceAsStream("/fonts/NotoSans-Regular.ttf"),
                    "NotoSans"
            );

            builder.withHtmlContent(html, null);
            builder.toStream(os);

            builder.run();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
	
	DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
	
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
	
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
	        html = html.replace("${invoiceDate}", formatDate(LocalDate.now()));
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
	        float originalAmount = booking.getFrontendAmount();

	        Integer discountPercent = booking.getFrontendDiscountPercentage() != null
	                ? booking.getFrontendDiscountPercentage()
	                : 0;

	        float discountedAmount = booking.getFrontendDiscountedAmount();

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
	        html = html.replace("${originalAmount}", format(originalAmount));
	        html = html.replace("${discountPercent}", String.valueOf(discountPercent));
	        html = html.replace("${discountedAmount}", format(discountedAmount));

	        // TAX
	        
	        html = html.replace("${cgstPercent}", String.valueOf(cgstPercent));
	        html = html.replace("${sgstPercent}", String.valueOf(sgstPercent));
	        
	        html = html.replace("${cgst}", format(cgstAmount));
	        html = html.replace("${sgst}", format(sgstAmount));
	        html = html.replace("${total}", format(total));

	        return html;

	    } catch (Exception e) {
	        throw new RuntimeException("Failed to build invoice template", e);
	    }
	}
	
	public static String format(Float num) {
		return  String.format("%.02f", num);
		
	}
	
	private String buildLineItems(Booking booking) {

	    StringBuilder rows = new StringBuilder();
	    int index = 1;
	    // 🔵 Conference Direct
	    if (booking instanceof ConferenceBookingDirect) {

	        ConferenceBookingDirect conf = (ConferenceBookingDirect) booking;

	        if (conf.getSlots() != null && !conf.getSlots().isEmpty()) {

	            List<ConferenceRoomTimeSlot> slots = conf.getSlots();

	            // Sort just in case slots aren't ordered
	            slots.sort(Comparator.comparing(ConferenceRoomTimeSlot::getStartTime));

	            LocalTime overallStartTime = slots.get(0).getStartTime();
	            LocalTime overallEndTime = slots.get(slots.size() - 1).getEndTime();

	            String startTimeStr = overallStartTime.format(timeFormatter);
	            String endTimeStr = overallEndTime.format(timeFormatter);

	            rows.append(buildRowConferenceBookingDirect(
	                    index++,
	                    "Conference Room Booking (" + startTimeStr + " - " + endTimeStr + ")",
	                    conf.getConferenceRoom().getName(),
	                    conf.getLetsWorkCentre().getName(),
	                    conf.getAppliedOffer() == null
	                            ? "NA"
	                            : conf.getAppliedOffer().getCode(),
	                    formatDate(conf.getStartDate())
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
	                formatDate(bundle.getDateOfPurchase().toLocalDate()),
	                bundle.getRemainingHours().intValue(),
	                formatDate(bundle.getExpiryDate())
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
	                "Day pass Booking Direct ("+dp.getLetsWorkCentre().getName()+")",
	                dp.getNumberOfPasses(),
	                formatDate(booking.getStartDate()),
	                dp.getAppliedOffer()==null?"NA": dp.getAppliedOffer().getCode()
	                	        ));
	    }

	    // 🟣 Day Pass Bundle (FULL PURCHASE - NOT USAGE)
	    else if (booking instanceof DayPassBundleBooking) {
	        DayPassBundleBooking dpb = (DayPassBundleBooking) booking;
	        rows.append(buildRowDayPassBundleBooking(
	                index++,
	                "Day Pass Bundle Purchase ("+dpb.getLetsWorkCentre().getName()+")",
	                formatDate(dpb.getExpiryDate()),
	                dpb.getRemainingNumberOfDays()
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
	        String appliedOffer,
	        String dateOfUse
	       
	        
	) {
        
	    return "<tr>" +
	            "<td>" + index + "</td>" +
	            "<td>" + description + "</td>" +
	            "<td>" + name + "</td>" +
	            "<td>" + center + "</td>" +
	            "<td>" + appliedOffer + "</td>" +
	            "<td>" + dateOfUse + "</td>" +
	            "</tr>";
	}
	
	private String buildRowConferenceBundleBooking(
	        int index,
	        String description,
	        Long ConferenceBundleId,
	        String dateOfPurchase,
	        int remainingHours,
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
	        String appliedOffer 
	        
	) {
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
       // String expiryDate2 = startDate.format(formatter);
        
	    return "<tr>" +
	            "<td>" + index + "</td>" +
	            "<td>" + description + "</td>" +
	            "<td>" + numberOfPasses + "</td>" +
	            "<td>" + startDate + "</td>" +
	            "<td>" + appliedOffer + "</td>" +
//	            "<td>" + previousBookingId + "</td>" +
	            "</tr>";
	}
	
	private String buildRowDayPassBundleBooking(
	        int index,
	        String description,
	        String expiryDate,
	        Integer remainingNumberOfDays
	        
	) {
	    return "<tr>" +
	            "<td>" + index + "</td>" +
	            "<td>" + description + "</td>" +
//	            "<td>" + hsn + "</td>" +
	            "<td>" + expiryDate + "</td>" +
	            "<td>" + remainingNumberOfDays + "</td>" +
	            
	            "</tr>";
	}
	
	public static String formatDate(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy");
		return date.format(formatter);
	}
	
	public static String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy");
		return formatter.format(date);
	}

}
