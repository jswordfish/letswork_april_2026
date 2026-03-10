package com.letswork.crm.serviceImpl;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.AllBookings;
import com.letswork.crm.entities.BuyConferenceBundle;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.entities.Holiday;
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.User;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.BookingType;
import com.letswork.crm.enums.InvoiceStatus;
import com.letswork.crm.repo.AllBookingsRepository;
import com.letswork.crm.repo.BuyConferenceBundleRepository;
import com.letswork.crm.repo.ConferenceRoomRepository;
import com.letswork.crm.repo.ConferenceRoomTimeSlotRepository;
import com.letswork.crm.repo.HolidayRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.service.AllBookingsService;
import com.letswork.crm.service.InvoiceService;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.service.QRCodeService;

@Service
public class AllBookingsServiceImpl implements AllBookingsService {
	
	@Autowired
    ConferenceRoomTimeSlotRepository timeSlotRepo;
	
	@Autowired
	PdfService pdfService;

    @Autowired
    HolidayRepository holidayRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    BuyConferenceBundleRepository bundleRepo;

    @Autowired
    ConferenceRoomRepository conferenceRoomRepo;

    @Autowired
    NewUserRegisterService newUserRegisterService;

    @Autowired
    QRCodeService qrService;

    @Autowired
    S3Service s3Service;

    @Autowired
    AllBookingsRepository repository;
    
    @Autowired
    private MailJetOtpService mailService;

    @Autowired
    private LetsWorkClientRepository letsWorkClientRepository;
   

    @Override
    public AllBookings createDayPassBooking(AllBookings request) {

        request.setBookingCode(UUID.randomUUID().toString());
        request.setBookingType(BookingType.DAY_PASS);
        request.setCurrentStatus(BookingStatus.DRAFT);
        request.setUsed(0);
        request.setCreateDate(new Date());
        String uuid = UUID.randomUUID().toString();
        String ref = "DAYPASS-"+uuid;
        request.setReferenceId(ref);
        
        File qrFile;

        try {

            String qrPath = qrService.generateQRCodeWithBookingCodeRGB(
                    "DAYPASS|" + request.getBookingCode()
            );

            qrFile = new File(qrPath);

            String s3Path = s3Service.uploadBookDayPassQrCode(
                    "letsworkcentres",
                    request.getCompanyId(),
                    request.getEmail(),
                    request.getBookingCode(),
                    qrFile
            );

            request.setQrS3Path(s3Path);

        } catch (Exception e) {
            throw new RuntimeException("QR generation failed", e);
        }

        AllBookings saved = repository.save(request);

        createInvoice(saved);

        sendBookingEmail(saved);

        return saved;
    }
    
    private void sendBookingEmail(AllBookings booking) {

        LetsWorkClient client = letsWorkClientRepository
                .findByEmailAndCompanyId(
                        booking.getEmail(),
                        booking.getCompanyId())
                .orElseThrow(() ->
                        new RuntimeException("Client not found"));

        mailService.sendDayPassEmail(
                booking.getEmail(),
                booking.getNumberOfDays(),
                booking.getId(),
                booking.getLetsWorkCentre(),
                booking.getQrS3Path(),
                client.getClientCompanyName()
        );
    }

    private void createInvoice(AllBookings booking) {

        Invoice invoice = new Invoice();

        invoice.setCompanyId(booking.getCompanyId());
        invoice.setCompanyEmail(booking.getEmail());
        invoice.setAmount(booking.getAmount());
        invoice.setBookingId(booking.getId());
        invoice.setBookingType(booking.getBookingType());

        if (booking.getBookedFrom() == BookedFrom.ADMIN) {
            invoice.setInvoiceStatus(InvoiceStatus.UNPAID);
        } else {
            invoice.setInvoiceStatus(InvoiceStatus.PAID);
        }

        invoice.setCreateDate(new Date());

        // 1️⃣ Save invoice first
        Invoice savedInvoice = invoiceService.saveInvoice(invoice);

        try {

            // 2️⃣ Build HTML
            String html = pdfService.buildInvoiceHtml(savedInvoice);

            // 3️⃣ Generate PDF
            byte[] pdfBytes = pdfService.generateInvoicePdf(html);

            // 4️⃣ Upload to S3
            String keyName = s3Service.uploadInvoicePdf(
                    "letsworkcentres",
                    savedInvoice.getCompanyId(),
                    savedInvoice.getId(),
                    pdfBytes
            );

            // 5️⃣ Save S3 key in DB
            savedInvoice.setPdfS3KeyName(keyName);

            invoiceService.saveInvoice(savedInvoice);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Invoice PDF generation failed", e
            );

        }
    }
    
    

    @Override
    public PaginatedResponseDto getBookings(
            String companyId,
            String email,
            String centre,
            String city,
            String state,
            BookingType bookingType,
            BookingStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createDate").descending()
        );

        Page<AllBookings> resultPage = repository.filter(
                companyId,
                email,
                centre,
                city,
                state,
                bookingType,
                status,
                fromDate,
                toDate,
                pageable
        );

        PaginatedResponseDto dto = new PaginatedResponseDto();

        dto.setSelectedPage(page);

        dto.setTotalNumberOfRecords((int) resultPage.getTotalElements());

        dto.setTotalNumberOfPages(resultPage.getTotalPages());

        dto.setRecordsFrom(page * size + 1);

        dto.setRecordsTo(
                Math.min(
                        (page + 1) * size,
                        (int) resultPage.getTotalElements()
                )
        );

        dto.setList(resultPage.getContent());

        return dto;
    }
    
    @Override
    public AllBookings createConferenceRoomBooking(
            AllBookings request,
            LocalDate slotDate,
            List<ConferenceRoomSlotRequest> slotRequests
    ) {

        ConferenceRoom room = conferenceRoomRepo
                .findByNameAndLetsWorkCentreAndCompanyIdAndCityAndState(
                        request.getRoomName(),
                        request.getLetsWorkCentre(),
                        request.getCompanyId(),
                        request.getCity(),
                        request.getState()
                );

        if (room == null) {
            throw new RuntimeException("Conference room does not exist");
        }

        validateHoliday(
                request.getCompanyId(),
                request.getLetsWorkCentre(),
                request.getCity(),
                request.getState(),
                slotDate
        );

        validateAdminBooking(
                request.getBookedFrom(),
                request.getAdminEmail(),
                request.getCompanyId()
        );

        validateConsecutiveSlots(slotRequests);

        for (ConferenceRoomSlotRequest slot : slotRequests) {

            if (timeSlotRepo
                    .existsByCompanyIdAndLetsWorkCentreAndCityAndStateAndRoomNameAndSlotDateAndStartTime(
                            request.getCompanyId(),
                            request.getLetsWorkCentre(),
                            request.getCity(),
                            request.getState(),
                            request.getRoomName(),
                            slotDate,
                            slot.getStartTime()
                    )) {

                throw new RuntimeException(
                        "One or more selected slots already booked"
                );
            }
        }

        int creditsRequired = slotRequests.size();
        float hours = creditsRequired / 2.0f;

        request.setNumberOfHours(hours);
        request.setBookingCode(UUID.randomUUID().toString());
        request.setBookingType(BookingType.CONFERENCE_ROOM);
        request.setDateOfBooking(slotDate);
        request.setCreateDate(new Date());
        request.setUsed(0);
        request.setCurrentStatus(BookingStatus.DRAFT);
        String uuid = UUID.randomUUID().toString();
        String ref = "CONF-"+uuid;
        request.setReferenceId(ref);

        if (Boolean.TRUE.equals(request.getBundleUsed())) {
            consumeConferenceCredits(creditsRequired, request);
        }

        AllBookings savedBooking = repository.save(request);

        saveSlots(savedBooking, slotDate, slotRequests);

        return generateQrAndInvoice(savedBooking);
    }
    
    private void saveSlots(
            AllBookings booking,
            LocalDate slotDate,
            List<ConferenceRoomSlotRequest> slotRequests
    ) {

        List<ConferenceRoomTimeSlot> slots = new ArrayList<>();

        for (ConferenceRoomSlotRequest s : slotRequests) {

            ConferenceRoomTimeSlot t = new ConferenceRoomTimeSlot();

            t.setCompanyId(booking.getCompanyId());
            t.setLetsWorkCentre(booking.getLetsWorkCentre());
            t.setCity(booking.getCity());
            t.setState(booking.getState());
            t.setRoomName(booking.getRoomName());

            t.setSlotDate(slotDate);
            t.setStartTime(s.getStartTime());
            t.setEndTime(s.getEndTime());

            t.setBooking(booking);

            slots.add(t);
        }

        timeSlotRepo.saveAll(slots);
    }
    
    private AllBookings generateQrAndInvoice(AllBookings booking) {

        try {

            String qrPath = qrService.generateQRCodeWithBookingCodeRGB(
                    "CONFROOM|" + booking.getBookingCode()
            );

            File qrFile = new File(qrPath);

            String s3Path = s3Service.uploadConferenceRoomQrCode(
                    "letsworkcentres",
                    booking.getCompanyId(),
                    booking.getEmail(),
                    booking.getBookingCode(),
                    qrFile
            );

            booking.setQrS3Path(s3Path);

            AllBookings finalSaved = repository.save(booking);

            createConferenceInvoice(
                    finalSaved.getCompanyId(),
                    finalSaved.getEmail(),
                    finalSaved.getAmount(),
                    finalSaved.getId(),
                    BookingType.CONFERENCE_ROOM
            );

            return finalSaved;

        } catch (Exception e) {

            throw new RuntimeException(
                    "QR generation failed: " + e.getMessage()
            );
        }
    }
    
    private void createConferenceInvoice(
            String companyId,
            String email,
            Integer amount,
            Long bookingId,
            BookingType bookingType
    ) {

        AllBookings booking = repository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Invoice invoice = new Invoice();

        invoice.setCompanyId(companyId);
        invoice.setCompanyEmail(email);
        invoice.setAmount(amount);
        invoice.setBookingId(bookingId);
        invoice.setBookingType(bookingType);

        if (booking.getBookedFrom() == BookedFrom.ADMIN) {
            invoice.setInvoiceStatus(InvoiceStatus.UNPAID);
        } else {
            invoice.setInvoiceStatus(InvoiceStatus.PAID);
        }

        invoice.setCreateDate(new Date());

        // 1️⃣ Save invoice first
        Invoice savedInvoice = invoiceService.saveInvoice(invoice);

        try {

            // 2️⃣ Generate HTML
            String html = pdfService.buildInvoiceHtml(savedInvoice);

            // 3️⃣ Generate PDF
            byte[] pdfBytes = pdfService.generateInvoicePdf(html);

            // 4️⃣ Upload to S3
            String keyName = s3Service.uploadInvoicePdf(
                    "letsworkcentres",
                    savedInvoice.getCompanyId(),
                    savedInvoice.getId(),
                    pdfBytes
            );

            // 5️⃣ Save S3 key
            savedInvoice.setPdfS3KeyName(keyName);

            invoiceService.saveInvoice(savedInvoice);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Conference invoice PDF generation failed", e
            );

        }
    }
    
    private void consumeConferenceCredits(
            int creditsRequired,
            AllBookings request
    ) {

        List<BuyConferenceBundle> bundles = bundleRepo.findActiveBundles(
                request.getEmail(),
                request.getCompanyId(),
                LocalDateTime.now()
        );

        if (bundles.isEmpty()) {
            throw new RuntimeException("No active conference bundles found");
        }

        int remainingToDeduct = creditsRequired;

        for (BuyConferenceBundle bundle : bundles) {

            float totalHoursInBundle = Float.parseFloat(bundle.getNumberOfHours());

            int availableCredits = Math.round(totalHoursInBundle * 2);

            if (availableCredits <= 0) continue;

            int creditsToTake = Math.min(availableCredits, remainingToDeduct);

            int updatedCredits = availableCredits - creditsToTake;

            float updatedHours = updatedCredits / 2.0f;

            bundle.setNumberOfHours(String.valueOf(updatedHours));

            bundleRepo.save(bundle);

            remainingToDeduct -= creditsToTake;

            if (remainingToDeduct <= 0) break;
        }

        if (remainingToDeduct > 0) {
            throw new RuntimeException(
                    "Insufficient conference credits. Missing: "
                            + remainingToDeduct
            );
        }
    }
    
    private void validateHoliday(
            String companyId,
            String centre,
            String city,
            String state,
            LocalDate bookingDate
    ) {

        Date holidayDate = java.sql.Date.valueOf(bookingDate);

        Holiday holiday = holidayRepo
                .findByLetsWorkCentreAndHolidayDateAndCityAndStateAndCompanyId(
                        centre,
                        holidayDate,
                        city,
                        state,
                        companyId
                );

        if (holiday != null) {

            throw new RuntimeException(
                    "Bookings are not allowed on holidays (" + bookingDate + ")"
            );
        }
    }
    
    private void validateAdminBooking(
            BookedFrom bookedFrom,
            String adminEmail,
            String companyId
    ) {

        if (BookedFrom.ADMIN.equals(bookedFrom)) {

            if (adminEmail == null || adminEmail.trim().isEmpty()) {
                throw new RuntimeException(
                        "Admin email required when booking by ADMIN"
                );
            }

            User admin = userRepo.findByEmail(adminEmail, companyId);
            
            if (admin == null) {
                throw new RuntimeException(
                        "Invalid admin email"
                );
            }
        }
    }
    
    private void validateConsecutiveSlots(
            List<ConferenceRoomSlotRequest> slots
    ) {

        if (slots == null || slots.isEmpty()) {
            throw new RuntimeException("No slots selected");
        }

        slots.sort(Comparator.comparing(
                ConferenceRoomSlotRequest::getStartTime
        ));

        for (int i = 0; i < slots.size(); i++) {

            ConferenceRoomSlotRequest s = slots.get(i);

            if (!s.getEndTime().equals(s.getStartTime().plusMinutes(30))) {

                throw new RuntimeException(
                        "Each slot must be 30 minutes"
                );
            }

            if (i > 0 &&
                    !slots.get(i - 1).getEndTime()
                            .equals(s.getStartTime())) {

                throw new RuntimeException(
                        "Slots must be consecutive"
                );
            }
        }
    }
    
    public AllBookings scanAndConsume(String bookingCode) {

        AllBookings booking =
                repository.findByBookingCode(bookingCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid booking code"
                                ));

        if (booking.getUsed() == 1) {

            throw new RuntimeException(
                    "Conference room already used"
            );
        }

        booking.setUsed(1);

        return repository.save(booking);
    }

}
