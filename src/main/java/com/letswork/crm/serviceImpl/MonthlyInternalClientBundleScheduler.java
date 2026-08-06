package com.letswork.crm.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.letswork.crm.entities.ConferenceBundle;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.entities.Contract;
import com.letswork.crm.entities.DayPassBundle;
import com.letswork.crm.entities.DayPassBundleBooking;
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.InvoiceStatus;
import com.letswork.crm.repo.ConferenceBundleBookingRepository;
import com.letswork.crm.repo.ConferenceBundleRepository;
import com.letswork.crm.repo.ContractRepository;
import com.letswork.crm.repo.DayPassBundleBookingRepository;
import com.letswork.crm.repo.DayPassBundleRepository;
import com.letswork.crm.repo.InvoiceRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonthlyInternalClientBundleScheduler {

    private static final String COMPANY_ID = "LW";

    private final ContractRepository contractRepo;
    private final LetsWorkCentreRepository centreRepo;
    private final ConferenceBundleRepository conferenceBundleRepo;
    private final DayPassBundleRepository dayPassBundleRepo;
    private final ConferenceBundleBookingRepository conferenceBundleBookingRepo;
    private final DayPassBundleBookingRepository dayPassBundleBookingRepo;
    private final RazorpayService razorpayService;
    private final InvoiceRepository invoiceRepo;
    private final PdfService pdfService;
    private final S3Service s3Service;
    private final MailJetOtpService emailService;

    // Runs at 00:05 on the 1st of every month
    @Scheduled(cron = "0 5 0 1 * *")
    @Transactional
    public void generateMonthlyInternalClientBundles() {
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(today);
        int daysInMonth = yearMonth.lengthOfMonth();
        String monthLabel = today.format(DateTimeFormatter.ofPattern("MMMM_yyyy")); // e.g. July_2026

        List<Contract> activeContracts = contractRepo.findAllActiveContractsWithClient();
        if (activeContracts.isEmpty()) {
            log.info("No active contracts found for {} — skipping monthly bundle generation", monthLabel);
            return;
        }

        // pre-load all centres once so we don't hit the DB per client to resolve a centre
        Map<String, LetsWorkCentre> centreByKey = centreRepo.findAllByCompanyId(COMPANY_ID).stream()
                .collect(Collectors.toMap(this::centreKey, c -> c, (a, b) -> a));

        // caches so we only create each distinct bundle once, not once per client
        Map<Integer, ConferenceBundle> conferenceBundleCache = new HashMap<>();
        Map<String, DayPassBundle> dayPassBundleCache = new HashMap<>();

        List<ConferenceBundleBooking> conferenceBookings = new ArrayList<>();
        List<DayPassBundleBooking> dayPassBookings = new ArrayList<>();

        int skippedNoCredits = 0;
        int skippedNoCentre = 0;

        for (Contract contract : activeContracts) {
            LetsWorkClient client = contract.getLetsWorkClient();
            Integer freeConferenceCredits = contract.getFreeConferenceCredits();
            Integer freeDayPass = contract.getFreeDayPass();

            // ---- Conference bundle ----
            if (freeConferenceCredits != null && freeConferenceCredits > 0) {
                ConferenceBundle conferenceBundle = conferenceBundleCache.computeIfAbsent(
                        freeConferenceCredits,
                        credits -> findOrCreateMonthlyConferenceBundle(credits, monthLabel, daysInMonth));
                conferenceBookings.add(buildConferenceBundleBooking(client, conferenceBundle));
            } else {
                skippedNoCredits++;
                log.debug("Client id={} has no freeConferenceCredits on active contract — skipping conference bundle", client.getId());
            }

            // ---- Day pass bundle ----
            if (freeDayPass != null && freeDayPass > 0) {
                String centreLookupKey = centreKey(contract.getLetsWorkCentre(), contract.getCity(), contract.getState());
                LetsWorkCentre centre = centreByKey.get(centreLookupKey);

                if (centre == null) {
                    skippedNoCentre++;
                    log.warn("No matching LetsWorkCentre for client id={} (centre='{}', city='{}', state='{}'). "
                                    + "Skipping day pass bundle booking for this client.",
                            client.getId(), contract.getLetsWorkCentre(), contract.getCity(), contract.getState());
                } else {
                    String bundleCacheKey = centreLookupKey + "|" + freeDayPass;
                    DayPassBundle dayPassBundle = dayPassBundleCache.computeIfAbsent(
                            bundleCacheKey,
                            k -> findOrCreateMonthlyDayPassBundle(centre, freeDayPass, daysInMonth));
                    dayPassBookings.add(buildDayPassBundleBooking(client, centre, dayPassBundle));
                }
            } else {
                log.debug("Client id={} has no freeDayPass on active contract — skipping day pass bundle", client.getId());
            }
        }

        conferenceBundleBookingRepo.saveAll(conferenceBookings);
        dayPassBundleBookingRepo.saveAll(dayPassBookings);

        log.info("Monthly bundle generation done for {}: {} contracts processed, {} conference bookings, "
                        + "{} day pass bookings, {} distinct conference bundles, {} distinct day pass bundles "
                        + "({} clients skipped: no credits, {} clients skipped: no centre match)",
                monthLabel, activeContracts.size(), conferenceBookings.size(), dayPassBookings.size(),
                conferenceBundleCache.size(), dayPassBundleCache.size(), skippedNoCredits, skippedNoCentre);
    }
    
 // Runs at 00:05 on the 1st of every month
    @Scheduled(cron = "0 5 0 1 * *")
    @Transactional
    public void generateMonthlyInvoices() {
        LocalDate today = LocalDate.now();
        String monthLabel = today.format(DateTimeFormatter.ofPattern("MMMM_yyyy"));

        List<Contract> activeContracts = contractRepo.findAllActiveContractsWithClient();
        if (activeContracts.isEmpty()) {
            log.info("No active contracts found for {} — skipping monthly invoice generation", monthLabel);
            return;
        }

        int createdCount = 0;
        int skippedNoFees = 0;
        int skippedPdfFailure = 0;
        int skippedEmailFailure = 0;

        for (Contract contract : activeContracts) {
            LetsWorkClient client = contract.getLetsWorkClient();
            Float feesPerMonth = contract.getFeesPerMonth();

            if (feesPerMonth == null || feesPerMonth <= 0) {
                skippedNoFees++;
                log.debug("Client id={} has no feesPerMonth on active contract — skipping monthly invoice", client.getId());
                continue;
            }

            Invoice invoice = new Invoice();
            invoice.setLetsWorkClient(client);
            invoice.setBooking(null);
            invoice.setDateOfCreation(today);
            invoice.setAmount(BigDecimal.valueOf(feesPerMonth));
            invoice.setAmountFinal(feesPerMonth.floatValue());
            invoice.setInvoiceStatus(InvoiceStatus.UNPAID);
            invoice.setMonthly(Boolean.TRUE);

            Invoice savedInvoice = invoiceRepo.save(invoice);

            byte[] pdfBytes;
            try {
                String html = pdfService.buildInvoiceHtml(savedInvoice);
                pdfBytes = pdfService.generateInvoicePdf(html);

                String s3Key = s3Service.uploadInvoicePdf("letsworkcentres", savedInvoice.getCompanyId(), savedInvoice.getId(), pdfBytes);

                savedInvoice.setPdfS3KeyName(s3Key);
                invoiceRepo.save(savedInvoice);

                createdCount++;
            } catch (Exception e) {
                skippedPdfFailure++;
                log.error("Invoice id={} created for client id={} but PDF generation/upload failed — invoice saved without pdfS3KeyName",
                        savedInvoice.getId(), client.getId(), e);
                continue;
            }

//            try {
//                String invoiceFileName = "invoice_" + savedInvoice.getId() + "_" + monthLabel + ".pdf";
//                emailService.sendMonthlyInvoiceEmail(
//                        client.getClientCompanyName(),
//                        client.getEmail(),
//                        pdfBytes,
//                        invoiceFileName
//                );
//            } catch (Exception e) {
//                skippedEmailFailure++;
//                log.error("Invoice id={} created and PDF uploaded for client id={} but email send failed",
//                        savedInvoice.getId(), client.getId(), e);
//            }
        }

        log.info("Monthly invoice generation done for {}: {} contracts processed, {} invoices created "
                        + "({} clients skipped: no fees configured, {} invoices saved but PDF/upload failed, "
                        + "{} invoices created but email failed)",
                monthLabel, activeContracts.size(), createdCount, skippedNoFees, skippedPdfFailure, skippedEmailFailure);
    }

    // ---------- Bundle creation (find-or-create, now parameterized by contract values) ----------

    private ConferenceBundle findOrCreateMonthlyConferenceBundle(Integer freeConferenceCredits, String monthLabel, int daysInMonth) {
        float numberOfHours = freeConferenceCredits.floatValue();
        String name = "free_credits_" + freeConferenceCredits + "_" + monthLabel;

        ConferenceBundle existing = conferenceBundleRepo.findByNameAndNumberOfHoursAndCompanyId(
                name, numberOfHours, COMPANY_ID);
        if (existing != null) {
            return existing;
        }
        ConferenceBundle bundle = new ConferenceBundle();
        bundle.setName(name);
        bundle.setNumberOfHours(numberOfHours);
        bundle.setPrice(BigDecimal.ZERO);
        bundle.setValidForDays(daysInMonth);
        bundle.setShowInApp(false);
        bundle.setCompanyId(COMPANY_ID);
        bundle.setCreateDate(new Date());
        bundle.setUpdateDate(new Date());
        bundle.setFreeCredit(Boolean.TRUE);
        return conferenceBundleRepo.save(bundle);
    }

    private DayPassBundle findOrCreateMonthlyDayPassBundle(LetsWorkCentre centre, Integer freeDayPass, int daysInMonth) {
        DayPassBundle existing = dayPassBundleRepo.findByLetsWorkCentreAndCompanyIdAndCityAndStateAndNumberOfDays(
                centre.getName(), COMPANY_ID, centre.getCity(), centre.getState(), freeDayPass);
        if (existing != null) {
            existing.setLetsWorkCentre(centre);
            return existing;
        }
        DayPassBundle bundle = new DayPassBundle();
        bundle.setLetsWorkCentre(centre);
        bundle.setNumberOfDays(freeDayPass);
        bundle.setValidForDays(daysInMonth);
        bundle.setDiscountPercentage(0);
        bundle.setPrice(BigDecimal.ZERO);
        bundle.setCompanyId(COMPANY_ID);
        bundle.setCreateDate(new Date());
        bundle.setUpdateDate(new Date());
        bundle.setFreeCredit(Boolean.TRUE);
        return dayPassBundleRepo.save(bundle);
    }

    // ---------- Booking builders (unchanged) ----------

    private ConferenceBundleBooking buildConferenceBundleBooking(LetsWorkClient client, ConferenceBundle bundle) {
        Date createDate = new Date();
        LocalDate expiryDate = LocalDate.now().plusDays(Math.max(0, bundle.getValidForDays() - 1));

        ConferenceBundleBooking booking = ConferenceBundleBooking.builder()
                .letsWorkClient(client)
                .conferenceBundle(bundle)
                .totalHours(bundle.getNumberOfHours())
                .remainingHours(bundle.getNumberOfHours())
                .price(bundle.getPrice())
                .amount(bundle.getPrice())
                .bookingStatus(BookingStatus.ACTIVE)
                .bookedFrom(BookedFrom.ADMIN)
                .referenceId(generate("CONF_BUNDLE"))
                .frontendFinalAmountAfterAddingTax(1f)
                .createDate(createDate)
                .expiryDate(expiryDate)
                .companyId(bundle.getCompanyId())
                .dateOfPurchase(LocalDateTime.now())
                .build();

//        String orderId = razorpayService.createOrder(booking.getFrontendFinalAmountAfterAddingTax(), booking.getReferenceId());
//        booking.setRazorpayOrderId(orderId);
        return booking;
    }

    private DayPassBundleBooking buildDayPassBundleBooking(LetsWorkClient client, LetsWorkCentre centre, DayPassBundle bundle) {
        Date createDate = new Date();
        LocalDate expiryDate = LocalDate.now().plusDays(Math.max(0, bundle.getValidForDays() - 1));

        DayPassBundleBooking booking = DayPassBundleBooking.builder()
                .companyId(bundle.getCompanyId())
                .dateOfPurchase(LocalDateTime.now())
                .letsWorkClient(client)
                .letsWorkCentre(centre)
                .dayPassBundleeId(bundle.getId())
                .price(bundle.getPrice())
                .amount(bundle.getPrice())
                .totalDays(bundle.getNumberOfDays())
                .remainingNumberOfDays(bundle.getNumberOfDays())
                .bookingStatus(BookingStatus.ACTIVE)
                .bookedFrom(BookedFrom.ADMIN)
                .referenceId(generate("DAYPASS_BUNDLE"))
                .createDate(createDate)
                .expiryDate(expiryDate)
                .frontendFinalAmountAfterAddingTax(1f)
                .build();

//        String orderId = razorpayService.createOrder(booking.getFrontendFinalAmountAfterAddingTax(), booking.getReferenceId());
//        booking.setRazorpayOrderId(orderId);
        return booking;
    }

    // ---------- Helpers ----------

    private String centreKey(String name, String city, String state) {
        return normalize(name) + "|" + normalize(city) + "|" + normalize(state);
    }

    private String centreKey(LetsWorkCentre centre) {
        return centreKey(centre.getName(), centre.getCity(), centre.getState());
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    public static String generate(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis();
    }
}