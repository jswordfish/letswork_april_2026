package com.letswork.crm.serviceImpl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.PaymentVerificationResponseDto;
import com.letswork.crm.entities.Booking;
import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.entities.DayPassBookingDirect;
import com.letswork.crm.entities.DayPassBundle;
import com.letswork.crm.entities.DayPassBundleBooking;
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.Payment;
import com.letswork.crm.entities.PaymentStatus;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.InvoiceStatus;
import com.letswork.crm.repo.BookingRepository;
import com.letswork.crm.repo.DayPassBundleRepository;
import com.letswork.crm.repo.InvoiceRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.PaymentRepository;
import com.letswork.crm.service.BookingService;
import com.letswork.crm.service.PaymentVerificationService;
import com.razorpay.RazorpayClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentVerificationServiceImpl implements PaymentVerificationService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PdfService pdfService;
    private final BookingService bookingService;
    private final S3Service s3Service;
    private final MailJetOtpService mailService;
    private final LetsWorkClientRepository clientRepo;
    private final DayPassBundleRepository dayPassBundleRepo;

    private String razorpayKeyId = "rzp_test_SKguaIWs4EkI1g";

    private String razorpayKeySecret = "o5AgXZLJftz4fmxO9neqOGn7";

    @Override
    @Transactional
    public PaymentVerificationResponseDto verifyAndProcessPayment(String paymentId, String referenceId) {
        try {
            if (paymentRepository.existsByPaymentId(paymentId)) {
                throw new RuntimeException("Payment already processed for paymentId: " + paymentId);
            }

            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(paymentId);

            String razorpayStatus = razorpayPayment.get("status");
            String razorpayPaymentId = razorpayPayment.get("id");

            Payment paymentEntity = new Payment();
            paymentEntity.setPaymentId(razorpayPaymentId);

            Booking booking = bookingRepository.findByReferenceId(referenceId)
                    .orElseThrow(() -> new RuntimeException("Booking not found for referenceId: " + referenceId));

            paymentEntity.setLetsWorkClient(booking.getLetsWorkClient());
            paymentEntity.setAmount(booking.getAmount());
            paymentEntity.setDescription("Payment for booking referenceId: " + referenceId);

            PaymentStatus mappedStatus = mapRazorpayStatus(razorpayStatus);
            paymentEntity.setPaymentStatus(mappedStatus);

            Booking savedBooking = booking; 
            
            Invoice finalInvoice = null;

            if ("captured".equalsIgnoreCase(razorpayStatus) || "authorized".equalsIgnoreCase(razorpayStatus)) {

                booking.setBookingStatus(BookingStatus.ACTIVE);
                
                savedBooking = bookingService.save(booking);

                Invoice invoice = new Invoice();
                invoice.setBooking(booking);
                invoice.setAmount(booking.getAmount());
                invoice.setAmountFinal(booking.getFrontendFinalAmountAfterAddingTax());
                invoice.setInvoiceStatus(InvoiceStatus.PAID);
                invoice.setCompanyId(booking.getCompanyId());
                Invoice savedInvoice = invoiceRepository.save(invoice);

                String html = pdfService.buildInvoiceHtml(savedInvoice);
                byte[] pdfBytes = pdfService.generateInvoicePdf(html);
                
                String s3Key = s3Service.uploadInvoicePdf("letsworkcentres", savedInvoice.getCompanyId(), savedInvoice.getId(), pdfBytes);
                
                savedInvoice.setPdfS3KeyName(s3Key);
                invoiceRepository.save(savedInvoice);

                paymentEntity.setInvoice(savedInvoice);
                finalInvoice = savedInvoice;
                
                byte[] invoicePdf = s3Service.downloadFile(savedInvoice.getPdfS3KeyName());

             // 🔽 COMMON DATA
             String email = booking.getLetsWorkClient().getEmail();
             String name = booking.getLetsWorkClient().getClientCompanyName();
             String reference = booking.getReferenceId();

             if (booking instanceof ConferenceBundleBooking) {

            	    ConferenceBundleBooking bundleBooking = (ConferenceBundleBooking) booking;
            	    
            	    LetsWorkClient client = bundleBooking.getLetsWorkClient();
            	    
            	    if (client.getPurchasedConferenceCredits() == null) {
                    	Float credits = Optional.ofNullable(client.getPurchasedConferenceCredits()).orElse(0f);
            			client.setPurchasedConferenceCredits(bundleBooking.getConferenceBundle().getNumberOfHours() + credits);
            			clientRepo.save(client);
            		}else {
            			Float credits = client.getPurchasedConferenceCredits();
            			client.setPurchasedConferenceCredits(bundleBooking.getConferenceBundle().getNumberOfHours() + credits);
            			clientRepo.save(client);
            		}

            	    mailService.sendConferenceBundlePurchaseEmail(
            	            email,
            	            name,
            	            bundleBooking.getRemainingHours(),
            	            booking.getAmount(),
            	            bundleBooking.getExpiryDate(),
            	            reference,
            	            invoicePdf
            	    );

            	} else if (booking instanceof ConferenceBookingDirect) {

            	    ConferenceBookingDirect directBooking = (ConferenceBookingDirect) booking;

            	    List<ConferenceRoomTimeSlot> slots = directBooking.getSlots();

            	    if (slots == null || slots.isEmpty()) {
            	        throw new RuntimeException("Slots missing for booking");
            	    }

            	    slots.sort(Comparator.comparing(ConferenceRoomTimeSlot::getStartTime));

            	    String startTime = slots.get(0).getStartTime().toString();
            	    String endTime = slots.get(slots.size() - 1).getEndTime().toString();

            	    mailService.sendConferenceDirectBookingEmail(
            	            email,
            	            name,
            	            directBooking.getLetsWorkCentre().getName(),
            	            directBooking.getStartDate(),
            	            startTime,
            	            endTime,
            	            reference,
            	            invoicePdf,
            	            directBooking.getQrS3Path()
            	    );
            	}
            	else if (booking instanceof DayPassBundleBooking) {

            	    DayPassBundleBooking bundleBooking = (DayPassBundleBooking) booking;
            	    
            	    LetsWorkClient client = bundleBooking.getLetsWorkClient();
            	    
            	    DayPassBundle bundle = dayPassBundleRepo.findById(bundleBooking.getDayPassBundleeId()).orElse(null);
            	    if(bundle!=null) {
            	    if (client.getPurchasedDayPassCredits() == null) {
            			Integer credits = Optional.ofNullable(client.getPurchasedDayPassCredits()).orElse(0);
            			client.setPurchasedDayPassCredits(bundle.getNumberOfDays() + credits);
            			clientRepo.save(client);
            		}else {
            			Integer credits = client.getPurchasedDayPassCredits();
            			client.setPurchasedDayPassCredits(bundle.getNumberOfDays() + credits);
            			clientRepo.save(client);
            		}
            	    }
            	    else throw new RuntimeException("Bundle is null for this booking");

            	    mailService.sendDayPassBundlePurchaseEmail(
            	            email,
            	            name,
            	            bundleBooking.getRemainingNumberOfDays(), 
            	            booking.getAmount(),
            	            bundleBooking.getDateOfPurchase(),
            	            bundleBooking.getExpiryDate(),
            	            bundleBooking.getLetsWorkCentre().getName(),
            	            reference,
            	            invoicePdf
            	    );

            	} else if (booking instanceof DayPassBookingDirect) {

            	    DayPassBookingDirect directBooking = (DayPassBookingDirect) booking;

            	    mailService.sendDayPassDirectBookingEmail(
            	            email,
            	            name,
            	            directBooking.getLetsWorkCentre().getName(),
            	            directBooking.getStartDate(),
            	            reference,
            	            directBooking.getNumberOfPasses(),
            	            invoicePdf,
            	            directBooking.getQrS3Path()
            	    );
            	}
             }
                
            

            paymentRepository.save(paymentEntity);

            PaymentVerificationResponseDto dto = new PaymentVerificationResponseDto();
            
            dto.setInvoice(finalInvoice);
            dto.setPayment(paymentEntity);
            dto.setRazorpayPayment(razorpayPayment.toString());
            dto.setBooking(savedBooking);

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Error while verifying and processing payment", e);
        }
    }
    

    private PaymentStatus mapRazorpayStatus(String razorpayStatus) {
        if ("captured".equalsIgnoreCase(razorpayStatus)) {
            return PaymentStatus.CAPTURED;
        }
        if ("authorized".equalsIgnoreCase(razorpayStatus)) {
            return PaymentStatus.AUTHORIZED;
        }
        return PaymentStatus.FAILED;
    }
}