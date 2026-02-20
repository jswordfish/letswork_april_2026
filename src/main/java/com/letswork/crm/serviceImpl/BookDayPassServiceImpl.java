package com.letswork.crm.serviceImpl;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.BookDayPass;
import com.letswork.crm.entities.BuyDayPassBundle;
import com.letswork.crm.entities.DayPassLimit;
import com.letswork.crm.entities.Holiday;
import com.letswork.crm.entities.User;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.repo.BookDayPassRepository;
import com.letswork.crm.repo.BuyDayPassBundleRepository;
import com.letswork.crm.repo.DayPassLimitRepo;
import com.letswork.crm.repo.HolidayRepository;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.service.BookDayPassService;
import com.letswork.crm.service.LetsWorkClientService;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.service.QRCodeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookDayPassServiceImpl implements BookDayPassService {
	
	@Autowired
	HolidayRepository holidayRepo;
	
	@Autowired
	DayPassLimitRepo dayPassLimitRepo;
	
	@Autowired
	UserRepo userRepo;

    private final BookDayPassRepository bookRepo;
    private final BuyDayPassBundleRepository bundleRepo;
    private final NewUserRegisterService newUserRegisterService;
    private final QRCodeService qrService;
    private final S3Service s3Service;
    private final LetsWorkClientService letsWorkClientService;

    @Override
    public BookDayPass book(BookDayPass request) {

        request.setBookingCode(UUID.randomUUID().toString());
        request.setUsed(0);
        
        validateHoliday(
                request.getCompanyId(),
                request.getLetsWorkCentre(),
                request.getCity(),
                request.getState(),
                request.getDateOfBooking()
        );
        
        validateAdminBooking(request.getBookedFrom(), request.getAdminEmail(), request.getCompanyId());

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


        return saved;
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
    
    private void validateAdminBooking(BookedFrom bookedFrom, String adminEmail, String companyId) {

        if (BookedFrom.ADMIN.equals(bookedFrom)) {

            if (adminEmail == null || adminEmail.trim().isEmpty()) {
                throw new RuntimeException("Admin email is required when booking is done by ADMIN");
            }

            User admin = userRepo.findByEmail(adminEmail, companyId);

            if (admin == null) {
                throw new RuntimeException("Invalid admin email for this company");
            }
        }
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

        letsWorkClientService.updateDayPass(
                String.valueOf(-request.getNumberOfDays()),
                request.getEmail(),
                companyId
        );
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
            int page,
            int size
    ) {
    	Pageable pageable =
    	        PageRequest.of(page, size, Sort.by("dateOfBooking").descending());

    	Page<BookDayPass> resultPage = bookRepo.filter(
    	        companyId,
    	        email,
    	        letsWorkCentre,
    	        city,
    	        state,
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
                Math.min((page + 1) * size, (int) resultPage.getTotalElements())
        );
        dto.setList(resultPage.getContent());

        return dto;
    }

	@Override
	public BookDayPass scanAndConsume(String bookingCode) {
		
		BookDayPass booking = bookRepo
                .findByBookingCode(bookingCode)
                .orElseThrow(() ->
                        new RuntimeException("Invalid or expired Day Pass")
                );

        if (Boolean.TRUE.equals(booking.getUsed())) {
            throw new RuntimeException("Day Pass already used");
        }

        

//        booking.setUsed(true);

        return bookRepo.save(booking);
    }
	
	@Override
	public Integer getRemainingDayPass(
	        String companyId,
	        String letsWorkCentre,
	        String city,
	        String state,
	        LocalDate date
	) {

	    DayPassLimit limit = dayPassLimitRepo
	            .findByLetsWorkCentreAndCompanyIdAndCityAndState(
	                    letsWorkCentre, companyId, city, state
	            );

	    if (limit == null) {
	        throw new RuntimeException("Day pass limit not configured for this centre");
	    }

	    Integer bookedCount = bookRepo.getTotalBookedDayPass(
	            companyId,
	            letsWorkCentre,
	            city,
	            state,
	            date
	    );

	    int remaining = limit.getMaxLimit() - bookedCount;

	    return Math.max(remaining, 0);
	}
		
}

