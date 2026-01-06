package com.letswork.crm.serviceImpl;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.letswork.crm.entities.BookConferenceRoom;
import com.letswork.crm.entities.BuyConferenceBundle;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.repo.BookConferenceRoomRepository;
import com.letswork.crm.repo.BuyConferenceBundleRepository;
import com.letswork.crm.repo.ConferenceRoomRepository;
import com.letswork.crm.service.BookConferenceRoomService;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.service.QRCodeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookConferenceRoomServiceImpl
        implements BookConferenceRoomService {

    private final BookConferenceRoomRepository bookRepo;
    private final BuyConferenceBundleRepository bundleRepo;
    private final ConferenceRoomRepository conferenceRoomRepo;
    private final NewUserRegisterService newUserRegisterService;
    private final QRCodeService qrService;
    private final S3Service s3Service;

    @Override
    public BookConferenceRoom book(BookConferenceRoom request) {

        // 1️⃣ Validate conference room exists
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

        request.setBookingCode(UUID.randomUUID().toString());
        request.setUsed(false);

        // 2️⃣ Consume bundle credits if used
        if (Boolean.TRUE.equals(request.getBundleUsed())) {
            consumeConferenceCredits(
                    request,
                    request.getCompanyId()
            );
        }

        // 3️⃣ Generate QR & upload
        try {
            String qrPath = qrService.generateQRCodeWithBookingCodeRGB(
                    "CONFROOM|" + request.getBookingCode()
            );

            File qrFile = new File(qrPath);

            String s3Path =
                    s3Service.uploadConferenceRoomQrCode(
                            "letsworkcentres",
                            request.getCompanyId(),
                            request.getEmail(),
                            request.getBookingCode(),
                            qrFile
                    );

            request.setQrS3Path(s3Path);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to generate/upload QR code",
                    e
            );
        }

        return bookRepo.save(request);
    }

    // 🔥 CREDIT CONSUMPTION LOGIC
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
}