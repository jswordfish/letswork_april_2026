package com.letswork.crm.serviceImpl;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.letswork.crm.entities.BookDayPass;
import com.letswork.crm.entities.BuyDayPassBundle;
import com.letswork.crm.repo.BookDayPassRepository;
import com.letswork.crm.repo.BuyDayPassBundleRepository;
import com.letswork.crm.service.BookDayPassService;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.service.QRCodeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookDayPassServiceImpl implements BookDayPassService {

    private final BookDayPassRepository bookRepo;
    private final BuyDayPassBundleRepository bundleRepo;
    private final NewUserRegisterService newUserRegisterService;
    private final QRCodeService qrService;
    private final S3Service s3Service;

    @Override
    public BookDayPass book(BookDayPass request) {

        request.setBookingCode(UUID.randomUUID().toString());
        request.setUsed(false);

        if (Boolean.TRUE.equals(request.getBundleUsed())) {
            consumeBundleCredits(request, request.getCompanyId());
        }

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
            throw new RuntimeException("Failed to generate/upload QR code", e);
        }

        BookDayPass saved = bookRepo.save(request);

        
//        try {
//            byte[] qrBytes = Files.readAllBytes(qrFile.toPath());
//
//            mailJetDayPassService.sendDayPassEmail(
//                    saved.getEmail(),
//                    saved.getNumberOfDays(),
//                    saved.getBookingCode(),
//                    qrBytes
//            );
//
//        } catch (Exception e) {
//            // Email failure should NOT break booking
//            log.error("Failed to send Day Pass email for bookingCode={}",
//                    saved.getBookingCode(), e);
//        }

        return saved;
    }

    private void consumeBundleCredits(BookDayPass request, String companyId) {

        int remainingDays = request.getNumberOfDays();

        List<BuyDayPassBundle> bundles =
                bundleRepo.findActiveBundles(
                        request.getEmail(),
                        companyId,
                        request.getLetsWorkCentre(),
                        LocalDateTime.now()
                );

        if (bundles.isEmpty()) {
            throw new RuntimeException("No active day pass bundles found");
        }

        for (BuyDayPassBundle bundle : bundles) {

            int available = Integer.parseInt(bundle.getNumberOfDays());

            if (available <= 0) continue;

            int used = Math.min(available, remainingDays);

            bundle.setNumberOfDays(String.valueOf(available - used));
            bundleRepo.save(bundle);

            remainingDays -= used;

            if (remainingDays == 0) break;
        }

        if (remainingDays > 0) {
            throw new RuntimeException("Insufficient day pass credits");
        }

        newUserRegisterService.updateDayPass(
                String.valueOf(-request.getNumberOfDays()),
                request.getEmail(),
                companyId
        );
    }

    @Override
    public List<BookDayPass> get(
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
