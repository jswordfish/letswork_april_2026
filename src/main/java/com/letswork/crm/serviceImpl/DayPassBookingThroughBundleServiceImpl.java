package com.letswork.crm.serviceImpl;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.DayPassBookingThroughBundleEmailDto;
import com.letswork.crm.dtos.DayPassBookingThroughBundleRequest;
import com.letswork.crm.dtos.DayPassBundleUsageRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Booking;
import com.letswork.crm.entities.DayPassBookingDirect;
import com.letswork.crm.entities.DayPassBookingThroughBundle;
import com.letswork.crm.entities.DayPassBundleBooking;
import com.letswork.crm.entities.DayPassLimit;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByThroughBundle;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.BookingRepository;
import com.letswork.crm.repo.DayPassBookingThroughBundleRepository;
import com.letswork.crm.repo.DayPassBundleBookingRepository;
import com.letswork.crm.repo.DayPassLimitRepo;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.DayPassBookingThroughBundleService;
import com.letswork.crm.service.DayPassBundleBookingService;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.QRCodeService;
import com.letswork.crm.service.TenantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DayPassBookingThroughBundleServiceImpl implements DayPassBookingThroughBundleService {

	private final DayPassBundleBookingRepository dayPassBundleBookingRepository;
	private final QRCodeService qrService;
    private final S3Service s3Service;
    private final TenantService tenantService;
    private final LetsWorkCentreService letsWorkCentreService;
	private final DayPassBundleBookingService dayPassBundleBookingService;
	private final BookingRepository bookingRepo;
	private final MailJetOtpService mailService;
	private final DayPassLimitRepo dayPassLimitRepo;

//	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy");

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final DayPassBookingThroughBundleRepository dayPassBookingThroughBundleRepository;
	private final LetsWorkClientRepository clientRepo;

	@Transactional
	@Override
	public List<DayPassBookingThroughBundle> dayPassBookingThroughBundleBooking(
	        DayPassBookingThroughBundleRequest request) {

	    Tenant tenant = tenantService.findTenantByCompanyId(request.getCompanyId());

	    if (tenant == null) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CompanyId invalid - " + request.getCompanyId());
	    }

	    LetsWorkCentre centre = letsWorkCentreService.findById(request.getLetsworkCenterId());

	    if (centre == null) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This LetsWorkCentre does not exists");
	    }

	    LetsWorkClient client = clientRepo.findById(request.getClientId())
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found"));
	    
	    int totalRequestedPasses = request.getBundleUsages()
	            .stream()
	            .mapToInt(DayPassBundleUsageRequest::getDaysDeducted)
	            .sum();

	    Integer remainingPasses = getRemainingDayPass(
	            request.getCompanyId(),
	            centre.getName(),
	            centre.getCity(),
	            centre.getState(),
	            request.getDateOfUse()
	    );

	    if (remainingPasses <= 0) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Day pass limit reached for this date"
	        );
	    }

	    if (totalRequestedPasses > remainingPasses) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Only " + remainingPasses + " day passes remaining for this date"
	        );
	    }

	    List<DayPassBookingThroughBundle> bookings = new ArrayList<>();

	    // ✅ EMAIL QUEUE
	    List<DayPassBookingThroughBundleEmailDto> emailQueue = new ArrayList<>();

	    for (DayPassBundleUsageRequest usage : request.getBundleUsages()) {

	        DayPassBundleBooking bundle = dayPassBundleBookingRepository
	                .findById(usage.getDayPassBundleBookingId())
	                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle not found"));

	        int remaining = bundle.getRemainingNumberOfDays();

	        if (remaining < usage.getDaysDeducted()) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough days in bundle");
	        }

	        bundle.setCompanyId(request.getCompanyId());

	        // ✅ Create booking
	        DayPassBookingThroughBundle booking = new DayPassBookingThroughBundle();

	        booking.setDayPassBundleBookingId(bundle.getId());
	        booking.setCompanyId(bundle.getCompanyId());
	        booking.setNumberOfPasses(usage.getDaysDeducted());
	        booking.setLetsWorkClient(client);
	        booking.setLetsWorkCentre(centre);
	        booking.setCreateDate(new Date());
	        booking.setDateOfPurchase(LocalDateTime.now());
	        booking.setStartDate(request.getDateOfUse());
	        booking.setBookingStatus(BookingStatus.ACTIVE);
	        String refId = generate("DayPassBookingThroughBundle");
	        booking.setReferenceId(refId);
	        
	        File qrFile;
	        try {
	            String qrPath = qrService.generateQRCodeWithBookingCodeRGB(
	                    refId
	            );

	            qrFile = new File(qrPath);

	            String s3Path = s3Service.uploadBookDayPassQrCode(
	                    "letsworkcentres",
	                    request.getCompanyId(),
	                    client.getEmail(),
	                    refId,
	                    qrFile
	            );

	            booking.setQrS3Path(s3Path);
	            
	        } catch (Exception e) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to generate/upload QR code", e);
	        }

	        // Deduct bundle
	        dayPassBundleBookingService.deductBundleWithDays(
	                bundle.getId(),
	                usage.getDaysDeducted()
	        );

	        DayPassBookingThroughBundle savedBooking =
	                dayPassBookingThroughBundleRepository.save(booking);
	        
	        Integer currentCredits = Optional
                    .ofNullable(client.getPurchasedDayPassCredits())
                    .orElse(0);

            Integer daysToDeduct = Optional
                    .ofNullable(booking.getNumberOfPasses())
                    .orElse(0);

            client.setPurchasedDayPassCredits(currentCredits - daysToDeduct);
            
            clientRepo.save(client);

	        bookings.add(savedBooking);

	        // ✅ ADD TO EMAIL QUEUE
	        emailQueue.add(new DayPassBookingThroughBundleEmailDto(
	                client.getEmail(),
	                client.getClientCompanyName(),
	                centre.getName(),
	                request.getDateOfUse(),
	                savedBooking.getReferenceId(),
	                bundle.getReferenceId(), 
	                usage.getDaysDeducted(),
	                savedBooking.getQrS3Path()
	        ));
	    }

	    // ✅ SEND EMAILS AFTER LOOP
	    sendDayPassBundleEmails(emailQueue);

	    return bookings;
	}
	
	@Async
	private void sendDayPassBundleEmails(List<DayPassBookingThroughBundleEmailDto> emailQueue) {

	    for (DayPassBookingThroughBundleEmailDto dto : emailQueue) {
	        try {
	            mailService.sendDayPassBookingThroughBundleEmail(
	                    dto.getEmail(),
	                    dto.getName(),
	                    dto.getCentre(),
	                    dto.getDateOfUse(),
	                    dto.getBookingReference(),
	                    dto.getBundleReference(),
	                    dto.getNumberOfDays(),
	                    dto.getQrS3Path()
	            );
	        } catch (Exception e) {
	            // 🔥 DO NOT FAIL TRANSACTION
	            log.error("Failed to send DayPass Bundle email for booking: {}",
	                    dto.getBookingReference(), e);
	        }
	    }
	}

	public static String generate(String prefix) {

		return prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis();
	}

	@Override
	public PaginatedResponseDto searchAllDayPassBookingThroughBundle(String companyId, LocalDateTime date,
			LocalDateTime startDate, LocalDateTime endDate, Long centreId, Long bundleId, Integer days,
			SortFieldByThroughBundle sortFieldByThroughBundle, SortingOrder order, int page, int size) {

		String fieldName = FIELD_MAP.get(sortFieldByThroughBundle);
		Sort sort = order.equals(SortingOrder.DESC) ? Sort.by(fieldName).descending() : Sort.by(fieldName).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);
//		Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());

		Page<DayPassBookingThroughBundle> result = dayPassBookingThroughBundleRepository
				.searchAllDayPassBookingThroughBundle(companyId, date, startDate, endDate, centreId, bundleId, days,
						pageable);

//		Page<DayPassBookingDirect> result = dayPassBookingThroughBundleRepository.dayPassBookingThroughBundle(companyId,
//				clientId, status, centre, city, state, fromDate == null ? null : fromDate.atStartOfDay(),
//				toDate == null ? null : toDate.atTime(23, 59, 59),
//				pageable);

		return buildResponse(result, page, size);
//		return null;
	}

	private static final Map<SortFieldByThroughBundle, String> FIELD_MAP = Map.of(
			SortFieldByThroughBundle.ID,"id",
			SortFieldByThroughBundle.NUMBER_OF_PASSES, "numberOfPasses",
			SortFieldByThroughBundle.DATE_OF_PURCHASE, "dateOfPurchase",
			SortFieldByThroughBundle.DAYPASS_BUNDLE_BOOKINGID, "dayPassBundleBookingId");


	private PaginatedResponseDto buildResponse(Page<?> resultPage, int page, int size) {

		PaginatedResponseDto dto = new PaginatedResponseDto();
		dto.setSelectedPage(page);
		dto.setTotalNumberOfRecords((int) resultPage.getTotalElements());
		dto.setTotalNumberOfPages(resultPage.getTotalPages());
		dto.setRecordsFrom(page * size + 1);
		dto.setRecordsTo(Math.min((page + 1) * size, (int) resultPage.getTotalElements()));
		dto.setList(resultPage.getContent());

		return dto;
	}

	
	@Override
	public DayPassBookingThroughBundle rescheduleBookingThroughBundle(Long bookingId, LocalDate newDate,
			String companyId) {

		DayPassBookingThroughBundle existing = dayPassBookingThroughBundleRepository
				.findByIdAndCompanyId(bookingId, companyId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking not found"));

		if (existing.getBookingStatus().equals(BookingStatus.ACTIVE.toString()) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ACTIVE bookings can be rescheduled");
		}

		if (existing.getStartDate().equals(newDate)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New date must be different from current booking date");
		}

		cancelBookingThroughBundle(bookingId, companyId);

		Tenant tenant = tenantService.findTenantByCompanyId(companyId);

		if (tenant == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CompanyId invalid - " + companyId);
		}

		bookingRepo.findById(bookingId);

		LetsWorkCentre centre = letsWorkCentreService.findById(existing.getLetsWorkCentre().getId());

		if (centre == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This LetsWorkCentre does not exists");
		}

		LetsWorkClient client = clientRepo.findById(existing.getLetsWorkClient().getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found"));

		DayPassBookingThroughBundle booking = new DayPassBookingThroughBundle();

		booking.setCompanyId(existing.getCompanyId());
		booking.setLetsWorkClient(existing.getLetsWorkClient());
		booking.setLetsWorkCentre(existing.getLetsWorkCentre());
		booking.setPreviousBookingId(existing.getId());
		booking.setBookingStatus(BookingStatus.RESCHEDULED);

		String refId = generate("DayPassBookingThroughBundle");
		booking.setReferenceId(refId);
		booking.setDateOfPurchase(LocalDateTime.now());
		booking.setStartDate(newDate);
		booking.setNumberOfPasses(existing.getNumberOfPasses());
		booking.setDayPassBundleBookingId(existing.getDayPassBundleBookingId());
//		booking.setDiscountedPrice(existing.getDiscountedPrice());

		File qrFile;
		try {
			String qrPath = qrService.generateQRCodeWithBookingCodeRGB(refId);

			qrFile = new File(qrPath);

			String s3Path = s3Service.uploadBookDayPassQrCode("letsworkcentres", existing.getCompanyId(),
					client.getEmail(), refId, qrFile);

			booking.setQrS3Path(s3Path);

		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to generate/upload QR code", e);
		}
		//
		
		booking = dayPassBookingThroughBundleRepository.save(booking);

		return booking;
	}

	private void validateCancellationAllowed(LocalDate bookingDate) {

		LocalDate today = LocalDate.now();

		if (!today.isBefore(bookingDate)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking can only be cancelled at least one day before the booking date");
		}
	}
	
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
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Day pass limit not configured for this centre");
	    }

	    List<Class<? extends Booking>> types = List.of(
	            DayPassBookingDirect.class,
	            DayPassBookingThroughBundle.class
	    );

	    Integer bookedCount = bookingRepo.getTotalBookedDayPass(
	            companyId,
	            types,
	            letsWorkCentre,
	            city,
	            state,
	            date
	    );

	    int remaining = limit.getMaxLimit() - bookedCount;

	    return Math.max(remaining, 0);
	}


	@Override
	public DayPassBookingThroughBundle cancelBookingThroughBundle(Long id, String companyId) {

		DayPassBookingThroughBundle booking = dayPassBookingThroughBundleRepository.findByIdAndCompanyId(id, companyId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking not found"));

		if (booking.getBookingStatus() != BookingStatus.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ACTIVE bookings can be cancelled");
		}

		validateCancellationAllowed(booking.getStartDate());

		booking.setBookingStatus(BookingStatus.CANCELLED);

		return bookingRepo.save(booking);

	}
}
