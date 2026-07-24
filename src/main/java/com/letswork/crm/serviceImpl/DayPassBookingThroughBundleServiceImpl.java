package com.letswork.crm.serviceImpl;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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

import com.letswork.crm.dtos.BundleBookingCreditMapper;
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
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByThroughBundle;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.BookingRepository;
import com.letswork.crm.repo.DayPassBookingThroughBundleRepository;
import com.letswork.crm.repo.DayPassBundleBookingRepository;
import com.letswork.crm.repo.DayPassLimitRepo;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
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
	private final NewUserRegisterRepository newUserRegisterRepo;

	@Transactional
	@Override
	public List<DayPassBookingThroughBundle> dayPassBookingThroughBundleBooking(
	        DayPassBookingThroughBundleRequest request) {

	    // 1. Initial Validations
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
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Day pass limit reached for this date");
	    }

	    if (totalRequestedPasses > remainingPasses) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only " + remainingPasses + " day passes remaining for this date");
	    }

	    LocalDate today = LocalDate.now();
	    if (request.getDateOfUse().isBefore(today)) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking date cannot be in the past");
	    }

	    // 2. Initialize Single Booking
	    DayPassBookingThroughBundle singleBooking = new DayPassBookingThroughBundle();
	    singleBooking.setCompanyId(request.getCompanyId());
	    singleBooking.setNumberOfPasses(totalRequestedPasses);
	    singleBooking.setLetsWorkClient(client);
	    singleBooking.setLetsWorkCentre(centre);
	    singleBooking.setCreateDate(new Date());
	    singleBooking.setDateOfPurchase(LocalDateTime.now());
	    singleBooking.setStartDate(request.getDateOfUse());
	    singleBooking.setExpiryDate(request.getDateOfUse());
	    singleBooking.setBookingStatus(BookingStatus.ACTIVE);
	    
		singleBooking.setBookedByUserId(request.getBookedByUserId());
        
        NewUserRegister bookedByUser = newUserRegisterRepo.findById(request.getBookedByUserId()).orElse(null);
        
        singleBooking.setBookedByUser(bookedByUser);
	    
	    String refId = generate("DayPassBookingThroughBundle");
	    singleBooking.setReferenceId(refId);
	    singleBooking.setBookedFrom(BookedFrom.APP);

	    // 3. Generate One QR Code
	    try {
	        String qrPath = qrService.generateQRCodeWithBookingCodeRGB(refId);
	        File qrFile = new File(qrPath);
	        String s3Path = s3Service.uploadBookDayPassQrCode(
	                "letsworkcentres",
	                request.getCompanyId(),
	                client.getEmail(),
	                refId,
	                qrFile
	        );
	        singleBooking.setQrS3Path(s3Path);
	    } catch (Exception e) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to generate/upload QR code", e);
	    }

	    // 4. Process Bundles & Populate Mapping List
	    List<BundleBookingCreditMapper> bundleMappers = new ArrayList<>();
	    Long primaryBundleId = null;
	    String primaryBundleRefForEmail = "MULTIPLE_BUNDLES"; // Fallback for email DTO

	    for (DayPassBundleUsageRequest usage : request.getBundleUsages()) {
	        DayPassBundleBooking bundle = dayPassBundleBookingRepository
	                .findById(usage.getBundleBookingId())
	                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle not found"));

	        if (bundle.getRemainingNumberOfDays() < usage.getDaysDeducted()) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough days in bundle: " + bundle.getId());
	        }

	        // Deduct bundle
	        dayPassBundleBookingService.deductBundleWithDays(
	                bundle.getId(),
	                usage.getDaysDeducted()
	        );

	        if (primaryBundleId == null) {
	            primaryBundleId = bundle.getId();
	            primaryBundleRefForEmail = bundle.getReferenceId(); // Keep first ref for legacy email templates
	        }

	        // Add to JSON mapping list
	        BundleBookingCreditMapper mapper = new BundleBookingCreditMapper();
	        mapper.setBundleId(bundle.getId());
	        mapper.setBundleName("Day Pass Bundle"); // Replace with bundle.getBundleName() if available
	        mapper.setCreditsUsed(usage.getDaysDeducted());
	        bundleMappers.add(mapper);
	    }

	    // Attach mappings to trigger JSON serialization
	    singleBooking.setMultipleBundleList(bundleMappers);
	    singleBooking.setDayPassBundleBookingId(primaryBundleId); // Legacy DB column fallback

	    // 5. Deduct Total Client Credits Once
	    Integer currentCredits = Optional.ofNullable(client.getPurchasedDayPassCredits()).orElse(0);
	    client.setPurchasedDayPassCredits(currentCredits - totalRequestedPasses);
	    clientRepo.save(client);

	    // 6. Save Single Booking
	    singleBooking = dayPassBookingThroughBundleRepository.save(singleBooking);

	    // 7. Send Single Email
	    List<DayPassBookingThroughBundleEmailDto> emailQueue = new ArrayList<>();
	    emailQueue.add(new DayPassBookingThroughBundleEmailDto(
	            client.getEmail(),
	            client.getClientCompanyName(),
	            centre.getName(),
	            request.getDateOfUse(),
	            singleBooking.getReferenceId(),
	            primaryBundleRefForEmail, // Passing the primary bundle's reference ID
	            totalRequestedPasses,
	            singleBooking.getQrS3Path()
	    ));
	    
	    sendDayPassBundleEmails(emailQueue);

	    // Return as a singleton list to match your existing method signature
	    return Collections.singletonList(singleBooking);
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
			String companyId, String source) {

		DayPassBookingThroughBundle existing = dayPassBookingThroughBundleRepository
				.findByIdAndCompanyId(bookingId, companyId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking not found"));

		if (existing.getBookingStatus() != BookingStatus.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ACTIVE bookings can be rescheduled");
		}

		if (existing.getStartDate().equals(newDate)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New date must be different from current booking date");
		}

		cancelBookingThroughBundle(bookingId, companyId, source);

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
		LocalDate today = LocalDate.now();
	     
	     if (newDate.isBefore(today)) {
	         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking date cannot be in the past");
	     }
		booking.setStartDate(newDate);
		booking.setExpiryDate(newDate);
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
	public DayPassBookingThroughBundle cancelBookingThroughBundle(Long id, String companyId, String source) {

		DayPassBookingThroughBundle booking = dayPassBookingThroughBundleRepository.findByIdAndCompanyId(id, companyId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking not found"));

		if ( !((booking.getBookingStatus() == BookingStatus.ACTIVE) || (booking.getBookingStatus() == BookingStatus.RESCHEDULED) )) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ACTIVE/RESCHEDULED bookings can be cancelled");
		}
		
		if(source == null) {
		validateCancellationAllowed(booking.getStartDate());
		}

		booking.setBookingStatus(BookingStatus.CANCELLED);

		return bookingRepo.save(booking);

	}
}
