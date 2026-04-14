package com.letswork.crm.serviceImpl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.DayPassBookingDirectRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Booking;
import com.letswork.crm.entities.DayPassBookingDirect;
import com.letswork.crm.entities.DayPassBookingThroughBundle;
import com.letswork.crm.entities.DayPassLimit;
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.Offers;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.InvoiceStatus;
import com.letswork.crm.enums.SortFieldByDirect;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.BookingRepository;
import com.letswork.crm.repo.DayPassBookingDirectRepository;
import com.letswork.crm.repo.DayPassLimitRepo;
import com.letswork.crm.repo.InvoiceRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.OffersRepository;
import com.letswork.crm.service.DayPassBookingDirectService;
import com.letswork.crm.service.QRCodeService;
import com.letswork.crm.service.TenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DayPassBookingDirectServiceImpl implements DayPassBookingDirectService {

	private final TenantService tenantService;
	private final LetsWorkCentreRepository letsWorkCentreRepo;
	private final LetsWorkClientRepository clientRepo;
	private final DayPassBookingDirectRepository dayPassBookingDirectRepository;
	private final OffersRepository offersRepo;
	private final BookingRepository bookingRepo;
	private final DayPassLimitRepo dayPassLimitRepo;
	private final QRCodeService qrService;
    private final S3Service s3Service;
    private final InvoiceRepository invoiceRepository;
    private final PdfService pdfService;

	@Override
	public DayPassBookingDirect createBooking(DayPassBookingDirectRequest request) {

		// 1. Tenant validation
		Tenant tenant = tenantService.findTenantByCompanyId(request.getCompanyId());
		if (tenant == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid companyId");
		}

		// 2. Centre validation
		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(request.getCentre(),
				request.getCompanyId(), request.getCity(), request.getState());

		if (centre == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Centre not found");
		}

		// 3. Client
		LetsWorkClient client = clientRepo.findById(request.getClientId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found"));
		
		Integer remainingPasses = getRemainingDayPass(
	            request.getCompanyId(),
	            request.getCentre(),
	            request.getCity(),
	            request.getState(),
	            request.getDateOfUse()
	    );

	    if (remainingPasses <= 0) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Day pass limit reached for this date"
	        );
	    }

	    if (request.getNumberOfPasses() > remainingPasses) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Only " + remainingPasses + " day passes remaining for this date"
	        );
	    }

		BigDecimal totalPrice = request.getPrice().multiply(BigDecimal.valueOf(request.getNumberOfPasses())); 

		BigDecimal discountedPrice = totalPrice;
		Offers offer = null;

		if (request.getOfferId() != null) {
			offer = offersRepo.findById(request.getOfferId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid offer"));

			discountedPrice = applyOffer(totalPrice, offer);
		}
		// 4. Create booking
		DayPassBookingDirect booking = new DayPassBookingDirect();

		booking.setLetsWorkClient(client);
		booking.setLetsWorkCentre(centre);
		booking.setCompanyId(centre.getCompanyId());
		booking.setDateOfPurchase(LocalDateTime.now());
		booking.setBookingStatus(BookingStatus.DRAFT);
		String refId = generate("DAY_PASS_DIRECT");
		booking.setReferenceId(refId);
		booking.setPrice(totalPrice);
		booking.setNumberOfPasses(request.getNumberOfPasses());
		booking.setDiscountedPrice(discountedPrice);
		booking.setAmount(discountedPrice);
		booking.setAppliedOffer(offer);
		booking.setCreateDate(new Date());
		booking.setDateOfPurchase(LocalDateTime.now());
		booking.setStartDate(request.getDateOfUse());
		
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
		
		booking = dayPassBookingDirectRepository.save(booking);
		return booking;
	}

	public static String generate(String prefix) {

		return prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis();
	}

	private BigDecimal applyOffer(BigDecimal price, Offers offer) {
	    // 1. Convert the discount percentage (e.g., 10) to a BigDecimal
	    BigDecimal discountPercent = BigDecimal.valueOf(offer.getDiscount());
	    BigDecimal oneHundred = new BigDecimal("100");

	    // 2. Calculate the discount amount: (price * discount) / 100
	    // Note: We specify 2 decimal places and rounding for the division
	    BigDecimal discountAmount = price.multiply(discountPercent)
	                                     .divide(oneHundred, 2, RoundingMode.HALF_UP);

	    // 3. Subtract the discount from the original price
	    return price.subtract(discountAmount);
	}

	//

//	@Override
//	public PaginatedResponseDto searchAllDayPassBookingDirectService(String companyId, Long  letsWorkCentreId, LocalDateTime date, LocalDateTime startDate,
//			LocalDateTime endDate, Float minPrice, Float maxPrice, Integer passes, int page, int size) {
//
//		Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());
//
//		Page<DayPassBookingDirect> result = dayPassBookingDirectRepository.searchAllDayPassBookingDirect(companyId, letsWorkCentreId, date,
//				startDate, endDate, minPrice, maxPrice, passes, pageable);
//
//		return buildResponse(result, page, size);
//	}
	
	@Override
	public PaginatedResponseDto searchAllDayPassBookingDirectService(String companyId, Long letsWorkCentreId,
			LocalDateTime date, LocalDateTime startDate, LocalDateTime endDate, Float minPrice, Float maxPrice,
			Integer passes, SortFieldByDirect sortFieldByDirect, SortingOrder order, int page, int size) {

		String fieldName = FIELD_MAP.get(sortFieldByDirect);
		Sort sort = order.equals(SortingOrder.DESC) ? Sort.by(fieldName).descending() : Sort.by(fieldName).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);
//		Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());

		Page<DayPassBookingDirect> result = dayPassBookingDirectRepository.searchAllDayPassBookingDirect(companyId,
				letsWorkCentreId, date, startDate, endDate, minPrice, maxPrice, passes, pageable);

		return buildResponse(result, page, size);
	}

	private static final Map<SortFieldByDirect, String> FIELD_MAP = Map.of(
			SortFieldByDirect.ID, "id",
			SortFieldByDirect.PRICE, "price",
			SortFieldByDirect.DATE_OF_PURCHASE, "dateOfPurchase", SortFieldByDirect.NUMBER_OF_PASSES, "numberOfPasses",
			SortFieldByDirect.DISCOUNTED_PRICE, "discountedPrice");


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

	//

	@Override
	public DayPassBookingDirect rescheduleBookingDirect(Long bookingId, LocalDate newDate, String companyId) {

		DayPassBookingDirect existing = dayPassBookingDirectRepository.findByIdAndCompanyId(bookingId, companyId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking not found"));

		if (existing.getBookingStatus() != BookingStatus.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ACTIVE bookings can be rescheduled");
		}

		if (existing.getStartDate().equals(newDate)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"New date must be different from current booking date");
		}

		cancelBookingDirect(bookingId, companyId);

		DayPassBookingDirect booking = new DayPassBookingDirect();

		booking.setCompanyId(existing.getCompanyId());
		booking.setLetsWorkClient(existing.getLetsWorkClient());
		booking.setLetsWorkCentre(existing.getLetsWorkCentre());
		booking.setPreviousBookingId(existing.getId());
		booking.setBookingStatus(BookingStatus.ACTIVE);
		booking.setReferenceId(generate("DAY_PASS_DIRECT"));
		booking.setDateOfPurchase(LocalDateTime.now());
		booking.setStartDate(newDate);
		booking.setPrice(existing.getPrice());
		booking.setNumberOfPasses(existing.getNumberOfPasses());
		booking.setDiscountedPrice(existing.getDiscountedPrice());

		booking = dayPassBookingDirectRepository.save(booking);
//		
//		Invoice
		Invoice invoice = new Invoice();
		invoice.setBooking(booking);
		invoice.setAmount(booking.getAmount());
		invoice.setInvoiceStatus(InvoiceStatus.PAID);
		invoice.setCompanyId(booking.getCompanyId());
		Invoice savedInvoice = invoiceRepository.save(invoice);

		String html = pdfService.buildInvoiceHtml(savedInvoice);
		byte[] pdfBytes = pdfService.generateInvoicePdf(html);

		String s3Key = s3Service.uploadInvoicePdf("letsworkcentres", savedInvoice.getCompanyId(), savedInvoice.getId(),
				pdfBytes);

		savedInvoice.setPdfS3KeyName(s3Key);
		invoiceRepository.save(savedInvoice);
//	
//		
		return booking;
	}

	private void validateCancellationAllowed(LocalDate bookingDate) {

		LocalDate today = LocalDate.now();

		if (!today.isBefore(bookingDate)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking can only be cancelled at least one day before the booking date");
		}
	}
	
	//
	
	@Override
	public DayPassBookingDirect cancelBookingDirect(Long id, String companyId) {

		DayPassBookingDirect booking = dayPassBookingDirectRepository.findByIdAndCompanyId(id, companyId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking not found"));

		if (booking.getBookingStatus() != BookingStatus.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ACTIVE bookings can be cancelled");
		}

		validateCancellationAllowed(booking.getStartDate());

		booking.setBookingStatus(BookingStatus.CANCELLED);

		return dayPassBookingDirectRepository.save(booking);

	}

	
}
