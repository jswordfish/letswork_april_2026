package com.letswork.crm.serviceImpl;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.entities.BookConferenceRoom;
import com.letswork.crm.entities.BuyConferenceBundle;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.repo.BookConferenceRoomRepository;
import com.letswork.crm.repo.BuyConferenceBundleRepository;
import com.letswork.crm.repo.ConferenceRoomRepository;
import com.letswork.crm.repo.ConferenceRoomTimeSlotRepository;
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

        ConferenceRoom room =
                conferenceRoomRepo
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

        validateConsecutiveSlots(slotRequests);

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

        if (slotRequests.size() % 2 != 0) {
            throw new RuntimeException("Slots must complete full hours");
        }

        int hours = slotRequests.size() / 2;
        request.setNumberOfHours(hours);

        request.setBookingCode(UUID.randomUUID().toString());
        request.setUsed(false);
        request.setDateOfPurchase(LocalDateTime.now());
        request.setDateOfBooking(slotDate);

        if (Boolean.TRUE.equals(request.getBundleUsed())) {
            consumeConferenceCredits(request, request.getCompanyId());
        }

        BookConferenceRoom savedBooking = bookRepo.save(request);

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

        try {
            String qrPath =
                    qrService.generateQRCodeWithBookingCodeRGB(
                            "CONFROOM|" + savedBooking.getBookingCode()
                    );

            File qrFile = new File(qrPath);

            String s3Path =
                    s3Service.uploadConferenceRoomQrCode(
                            "letsworkcentres",
                            savedBooking.getCompanyId(),
                            savedBooking.getEmail(),
                            savedBooking.getBookingCode(),
                            qrFile
                    );

            savedBooking.setQrS3Path(s3Path);
            return bookRepo.save(savedBooking);

        } catch (Exception e) {
            throw new RuntimeException("QR generation failed", e);
        }
    }

    private void consumeConferenceCredits(
            BookConferenceRoom request,
            String companyId
    ) {

        int hoursRequired = request.getNumberOfHours();
        int creditsRequired = hoursRequired * 2;

        List<BuyConferenceBundle> bundles =
                bundleRepo.findActiveBundles(
                        request.getEmail(),
                        companyId,
                        LocalDateTime.now()
                );

        if (bundles.isEmpty()) {
            throw new RuntimeException(
                    "No active conference bundles found"
            );
        }

        int remainingCredits = creditsRequired;

        for (BuyConferenceBundle bundle : bundles) {

            int availableCredits =
                    Integer.parseInt(bundle.getNumberOfHours()) * 2;

            if (availableCredits <= 0) continue;

            int usedCredits =
                    Math.min(availableCredits, remainingCredits);

            int remainingHours =
                    (availableCredits - usedCredits) / 2;

            bundle.setNumberOfHours(
                    String.valueOf(remainingHours)
            );

            bundleRepo.save(bundle);

            remainingCredits -= usedCredits;

            if (remainingCredits == 0) break;
        }

        if (remainingCredits > 0) {
            throw new RuntimeException(
                    "Insufficient conference credits"
            );
        }

        // Update user credits
        newUserRegisterService.updateConferenceCredits(
                String.valueOf(-hoursRequired),
                request.getEmail(),
                companyId
        );
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
    public List<BookConferenceRoom> get(
            String companyId,
            String email,
            String letsWorkCentre,
            String city,
            String state,
            LocalDate date
    ) {

        return bookRepo.filter(
                companyId,
                email,
                letsWorkCentre,
                city,
                state,
                date
        );
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