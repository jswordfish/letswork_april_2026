package com.letswork.crm.serviceImpl;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.BookConferenceRoom;
import com.letswork.crm.entities.BuyConferenceBundle;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.entities.Holiday;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.repo.BookConferenceRoomRepository;
import com.letswork.crm.repo.BuyConferenceBundleRepository;
import com.letswork.crm.repo.ConferenceRoomRepository;
import com.letswork.crm.repo.ConferenceRoomTimeSlotRepository;
import com.letswork.crm.repo.HolidayRepository;
import com.letswork.crm.service.BookConferenceRoomService;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.service.QRCodeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookConferenceRoomServiceImpl
        implements BookConferenceRoomService {
	
	@Autowired
	ConferenceRoomTimeSlotRepository timeSlotRepo;
	
	@Autowired
	HolidayRepository holidayRepo;

    private final BookConferenceRoomRepository bookRepo;
    private final BuyConferenceBundleRepository bundleRepo;
    private final ConferenceRoomRepository conferenceRoomRepo;
    private final NewUserRegisterService newUserRegisterService;
    private final QRCodeService qrService;
    private final S3Service s3Service;

    @Override
    public BookConferenceRoom book(
            BookConferenceRoom request,
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

        // 1. Validate consecutive slots
        validateConsecutiveSlots(slotRequests);

        // 2. Check slot availability
        for (ConferenceRoomSlotRequest slot : slotRequests) {
            if (timeSlotRepo.existsByCompanyIdAndLetsWorkCentreAndCityAndStateAndRoomNameAndSlotDateAndStartTime(
                    request.getCompanyId(),
                    request.getLetsWorkCentre(),
                    request.getCity(),
                    request.getState(),
                    request.getRoomName(),
                    slotDate,
                    slot.getStartTime()
            )) {
                throw new RuntimeException("One or more selected slots already booked");
            }
        }

        // 3. Logic: 1 slot = 1 credit | 2 credits = 1 hour
        int creditsRequired = slotRequests.size();
        float hours = creditsRequired / 2.0f;

        request.setNumberOfHours(hours);
        request.setBookingCode(UUID.randomUUID().toString());
        request.setUsed(false);
        request.setDateOfPurchase(LocalDateTime.now());
        request.setDateOfBooking(slotDate);

        // 4. Handle Credit Consumption
        if (Boolean.TRUE.equals(request.getBundleUsed())) {
            consumeConferenceCredits(creditsRequired, request);
        }
        
        request.setCurrentStatus(BookingStatus.ACTIVE);

        BookConferenceRoom savedBooking = bookRepo.save(request);

        // 5. Save booked slots
        List<ConferenceRoomTimeSlot> slots = new ArrayList<>();
        for (ConferenceRoomSlotRequest s : slotRequests) {
            ConferenceRoomTimeSlot t = new ConferenceRoomTimeSlot();
            t.setCompanyId(request.getCompanyId());
            t.setLetsWorkCentre(request.getLetsWorkCentre());
            t.setCity(request.getCity());
            t.setState(request.getState());
            t.setRoomName(request.getRoomName());
            t.setSlotDate(slotDate);
            t.setStartTime(s.getStartTime());
            t.setEndTime(s.getEndTime());
            t.setBooking(savedBooking);
            slots.add(t);
        }
        timeSlotRepo.saveAll(slots);

        // 6. QR generation + upload
        try {
            String qrPath = qrService.generateQRCodeWithBookingCodeRGB(
                    "CONFROOM|" + savedBooking.getBookingCode()
            );

            File qrFile = new File(qrPath);

            String s3Path = s3Service.uploadConferenceRoomQrCode(
                    "letsworkcentres",
                    savedBooking.getCompanyId(),
                    savedBooking.getEmail(),
                    savedBooking.getBookingCode(),
                    qrFile
            );

            savedBooking.setQrS3Path(s3Path);
            return bookRepo.save(savedBooking);

        } catch (Exception e) {
            // Because of @Transactional, the credits deducted above will be restored if this fails
            throw new RuntimeException("QR generation failed: " + e.getMessage());
        }
    }
    
    private void validateHoliday(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            LocalDate bookingDate
    ) {

        Date holidayDate = java.sql.Date.valueOf(bookingDate);

        Holiday holiday = holidayRepo
                .findByLetsWorkCentreAndHolidayDateAndCityAndStateAndCompanyId(
                        letsWorkCentre,
                        holidayDate,
                        city,
                        state,
                        companyId
                );

        if (holiday != null) {
            throw new RuntimeException(
                    "Bookings are not allowed on holidays (" + bookingDate + ") for this centre"
            );
        }
    }

    private void consumeConferenceCredits(
            int creditsRequired,
            BookConferenceRoom request
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
            // Convert the String "Hours" from DB into "Credits" (Hours * 2)
            float totalHoursInBundle = Float.parseFloat(bundle.getNumberOfHours());
            int availableCreditsInBundle = Math.round(totalHoursInBundle * 2);

            if (availableCreditsInBundle <= 0) continue;

            // Determine how many credits to take from this specific bundle
            int creditsToTake = Math.min(availableCreditsInBundle, remainingToDeduct);

            // Calculate remaining credits and convert back to Hours (Credits / 2)
            int updatedCredits = availableCreditsInBundle - creditsToTake;
            float updatedHours = updatedCredits / 2.0f;

            // Save back as string
            bundle.setNumberOfHours(String.valueOf(updatedHours));
            bundleRepo.save(bundle);

            remainingToDeduct -= creditsToTake;

            if (remainingToDeduct <= 0) break;
        }

        if (remainingToDeduct > 0) {
            throw new RuntimeException("Insufficient conference credits. Missing: " + remainingToDeduct + " slots.");
        }
    }

    @Override
    public BookConferenceRoom scanAndConsume(
            String bookingCode
    ) {

        BookConferenceRoom booking =
                bookRepo.findByBookingCode(bookingCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid or expired booking"
                                )
                        );

        if (Boolean.TRUE.equals(booking.getUsed())) {
            throw new RuntimeException(
                    "Conference room already used"
            );
        }

        booking.setUsed(true);
        return bookRepo.save(booking);
    }
    
    @Override
    public PaginatedResponseDto getPaginated(
            String companyId,
            String email,
            String letsWorkCentre,
            String city,
            String state,
            LocalDate fromDate,
            LocalDate toDate,
            String roomName,
            BookingStatus currentStatus,   
            int page,
            int size
    ) {

    	Pageable pageable =
    	        PageRequest.of(page, size, Sort.by("dateOfBooking").descending());

    	Page<BookConferenceRoom> resultPage = bookRepo.filter(
    	        companyId,
    	        email,
    	        letsWorkCentre,
    	        city,
    	        state,
    	        fromDate,
    	        toDate,
    	        roomName,
    	        currentStatus,   
    	        pageable
    	);

        PaginatedResponseDto dto = new PaginatedResponseDto();
        dto.setSelectedPage(page);
        dto.setTotalNumberOfRecords((int) resultPage.getTotalElements());
        dto.setTotalNumberOfPages(resultPage.getTotalPages());
        dto.setRecordsFrom(page * size + 1);
        dto.setRecordsTo(
                Math.min((page + 1) * size, (int) resultPage.getTotalElements())
        );
        dto.setList(resultPage.getContent());

        return dto;
    }
    
    private void validateConsecutiveSlots(
            List<ConferenceRoomSlotRequest> slots
    ) {

        if (slots == null || slots.isEmpty()) {
            throw new RuntimeException("No slots selected");
        }

        slots.sort(Comparator.comparing(ConferenceRoomSlotRequest::getStartTime));

        for (int i = 0; i < slots.size(); i++) {

            ConferenceRoomSlotRequest s = slots.get(i);

            if (!s.getEndTime().equals(s.getStartTime().plusMinutes(30))) {
                throw new RuntimeException("Each slot must be 30 minutes");
            }

            if (i > 0 &&
                !slots.get(i - 1).getEndTime().equals(s.getStartTime())) {
                throw new RuntimeException("Slots must be consecutive");
            }
        }
    }
}