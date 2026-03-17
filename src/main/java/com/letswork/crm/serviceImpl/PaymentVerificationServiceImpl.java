package com.letswork.crm.serviceImpl;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.Booking;
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.entities.Payment;
import com.letswork.crm.entities.PaymentStatus;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.InvoiceStatus;
import com.letswork.crm.repo.BookingRepository;
import com.letswork.crm.repo.InvoiceRepository;
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
    // add S3 service later if needed

    private String razorpayKeyId = "rzp_test_SKguaIWs4EkI1g";

    private String razorpayKeySecret = "o5AgXZLJftz4fmxO9neqOGn7";

    @Override
    @Transactional
    public void verifyAndProcessPayment(String paymentId, String referenceId) {
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
//            JSONObject notes = razorpayPayment.get("notes");
//            String referenceId = notes.getString("referenceId");

            Booking booking = bookingRepository.findByReferenceId(referenceId)
                    .orElseThrow(() -> new RuntimeException("Booking not found for referenceId: " + referenceId));

            paymentEntity.setPaymentId(razorpayPaymentId);
            paymentEntity.setLetsWorkClient(booking.getLetsWorkClient());
            paymentEntity.setAmount(booking.getAmount());
            paymentEntity.setDescription("Payment for booking referenceId: " + referenceId);

            PaymentStatus mappedStatus = mapRazorpayStatus(razorpayStatus);
            paymentEntity.setPaymentStatus(mappedStatus);

            if ("captured".equalsIgnoreCase(razorpayStatus) || "authorized".equalsIgnoreCase(razorpayStatus)) {

                booking.setBookingStatus(BookingStatus.ACTIVE);
                bookingService.save(booking);
              
                Invoice invoice = new Invoice();
                invoice.setBooking(booking);
                invoice.setAmount(booking.getAmount().intValue());
                invoice.setInvoiceStatus(InvoiceStatus.PAID);

                Invoice savedInvoice = invoiceRepository.save(invoice);

                String html = pdfService.buildInvoiceHtml(savedInvoice);
                byte[] pdfBytes = pdfService.generateInvoicePdf(html);

                // Later upload pdfBytes to S3 and set S3 key
                // String s3Key = s3Service.upload(pdfBytes, ...);
                // savedInvoice.setPdfS3KeyName(s3Key);

                invoiceRepository.save(savedInvoice);

                paymentEntity.setInvoice(savedInvoice);
            }

            paymentRepository.save(paymentEntity);

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