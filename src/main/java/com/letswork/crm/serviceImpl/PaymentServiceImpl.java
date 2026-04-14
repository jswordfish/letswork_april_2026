package com.letswork.crm.serviceImpl;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.Invoice;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.repo.InvoiceRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.PaymentService;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final InvoiceRepository invoiceRepository;
    private final LetsWorkClientRepository clientRepository;
    private final RazorpayClient razorpayClient;

    @Override
    public JSONObject createPaymentLink(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        LetsWorkClient client = clientRepository
                .findByEmailAndCompanyId(invoice.getBooking().getLetsWorkClient().getEmail(), invoice.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        try {

            JSONObject request = new JSONObject();

            BigDecimal paiseAmount = invoice.getAmount().multiply(new BigDecimal("100"));

	        request.put("amount", paiseAmount.longValue()); // paise
            request.put("currency", "INR");
            request.put("accept_partial", false);
//            request.put("description", "Payment for " + invoice.getBookingType());
            request.put("reference_id", "INV_" + invoice.getId());

            JSONObject customer = new JSONObject();
            customer.put("name", client.getClientCompanyName());
            customer.put("contact", client.getPhone());
            customer.put("email", client.getEmail());

            request.put("customer", customer);

            request.put("notify", new JSONObject()
                    .put("sms", true)
                    .put("email", true));

            request.put("reminder_enable", true);

            request.put("callback_url", "https://your-domain.com/payment/callback");
            request.put("callback_method", "get");

            PaymentLink paymentLink = razorpayClient.paymentLink.create(request);

            return new JSONObject(paymentLink.toString());

        } catch (RazorpayException e) {
            throw new RuntimeException("Error creating payment link", e);
        }
    }
}
