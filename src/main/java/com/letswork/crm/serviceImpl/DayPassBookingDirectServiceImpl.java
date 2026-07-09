package com.letswork.crm.serviceImpl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.BundleBookingCreditMapper;
import com.letswork.crm.dtos.DayPassBookingDirectRequest;
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
import com.letswork.crm.entities.Offers;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByDirect;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.BookingRepository;
import com.letswork.crm.repo.DayPassBookingDirectRepository;
import com.letswork.crm.repo.DayPassBundleBookingRepository;
import com.letswork.crm.repo.DayPassLimitRepo;
import com.letswork.crm.repo.InvoiceRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.repo.OffersRepository;
import com.letswork.crm.service.DayPassBookingDirectService;
import com.letswork.crm.service.DayPassBundleBookingService;
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
    private final RazorpayService razorpayService;
    private final DayPassBundleBookingRepository dayPassBundleBookingRepository;
    private final DayPassBundleBookingService dayPassBundleBookingService;
    private final NewUserRegisterRepository newUserRegisterRepo;

    @Transactional
    @Override
    public DayPassBookingDirect createBooking(DayPassBookingDirectRequest request) {

        // 1. Tenant validation
        Tenant tenant = tenantService.findTenantByCompanyId(request.getCompanyId());
        if (tenant == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid companyId");
        }

        // 2. Centre validation
        LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
                request.getCentre(), request.getCompanyId(), request.getCity(), request.getState());
        if (centre == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Centre not found");
        }

        // 3. Client
        LetsWorkClient client = clientRepo.findById(request.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found"));
        
        // 4. Availability Check
        Integer remainingPasses = getRemainingDayPass(
                request.getCompanyId(), request.getCentre(), request.getCity(), 
                request.getState(), request.getDateOfUse()
        );

        if (remainingPasses <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Day pass limit reached for this date");
        }

        if (request.getNumberOfPasses() > remainingPasses) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Only " + remainingPasses + " day passes remaining for this date");
        }
        
        LocalDate today = LocalDate.now();
        if (request.getDateOfUse().isBefore(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking date cannot be in the past");
        }

        // 5. Handle Bundles & Credits (Hybrid Logic)
        int passesCoveredByBundles = 0;
        List<BundleBookingCreditMapper> bundleMappers = new ArrayList<>();

        if (request.getBundleUsages() != null && !request.getBundleUsages().isEmpty()) {
            for (DayPassBundleUsageRequest usage : request.getBundleUsages()) {
                
                // Assuming dayPassBundleBookingRepository is injected in this service
                DayPassBundleBooking bundle = dayPassBundleBookingRepository.findById(usage.getBundleBookingId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle not found"));

                if (bundle.getRemainingNumberOfDays() < usage.getDaysDeducted()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough days in bundle: " + bundle.getId());
                }

                // Deduct from specific bundle
                dayPassBundleBookingService.deductBundleWithDays(bundle.getId(), usage.getDaysDeducted());
                passesCoveredByBundles += usage.getDaysDeducted();

                // Map the usage for JSON
                BundleBookingCreditMapper mapper = new BundleBookingCreditMapper();
                mapper.setBundleId(bundle.getId());
                mapper.setBundleName("Day Pass Bundle"); // Adjust if your entity has a getBundleName() method
                mapper.setCreditsUsed(usage.getDaysDeducted());
                bundleMappers.add(mapper);
            }

            // Deduct from client's global balance once
            Integer currentCredits = Optional.ofNullable(client.getPurchasedDayPassCredits()).orElse(0);
            client.setPurchasedDayPassCredits(currentCredits - passesCoveredByBundles);
            clientRepo.save(client);
        }

        // 6. Pricing Calculation
        BigDecimal totalPrice = request.getPrice().multiply(BigDecimal.valueOf(request.getNumberOfPasses())); 
        BigDecimal discountedPrice = totalPrice;
        Offers offer = null;

        if (request.getOfferId() != null) {
            offer = offersRepo.findById(request.getOfferId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid offer"));
            discountedPrice = applyOffer(totalPrice, offer);
        }

        // 7. Create booking
        DayPassBookingDirect booking = new DayPassBookingDirect();
        booking.setLetsWorkClient(client);
        booking.setLetsWorkCentre(centre);
        booking.setCompanyId(centre.getCompanyId());
        booking.setBookingStatus(request.getBookedFrom() == BookedFrom.APP ? BookingStatus.DRAFT : BookingStatus.ACTIVE);
        booking.setBookedFrom(request.getBookedFrom());
        
        String refId = generate("DAY_PASS_DIRECT");
        booking.setReferenceId(refId);
        booking.setPrice(totalPrice);
        booking.setNumberOfPasses(request.getNumberOfPasses());
        booking.setDiscountedPrice(discountedPrice);
        booking.setAmount(discountedPrice);
        booking.setAppliedOffer(offer);
        
        if(request.getBookedByUserId()!=null) {
            
            booking.setBookedByUserId(request.getBookedByUserId());
            
            NewUserRegister bookedByUser = newUserRegisterRepo.findById(request.getBookedByUserId()).orElse(null);
            
            booking.setBookedByUser(bookedByUser);
            }
        
        // Frontend calculation fields
        booking.setFrontendAmount(request.getFrontendAmount());
        booking.setFrontendDiscountPercentage(request.getFrontendDiscountPercentage());
        booking.setFrontendDiscountedAmount(request.getFrontendDiscountedAmount());
        booking.setFrontendCgstPercentage(request.getFrontendCgstPercentage());
        booking.setFrontendSgstPercentage(request.getFrontendSgstPercentage());
        booking.setFrontendFinalAmountAfterAddingTax(request.getFrontendFinalAmountAfterAddingTax());
        
        booking.setCreateDate(new Date());
        booking.setDateOfPurchase(LocalDateTime.now());
        booking.setStartDate(request.getDateOfUse());
        booking.setExpiryDate(request.getDateOfUse());

        // Attach the bundle mappings to trigger JSON serialization
        if (!bundleMappers.isEmpty()) {
            booking.setMultipleBundleList(bundleMappers);
        }

        // 🔥 Conditional Razorpay logic
        if (booking.getFrontendFinalAmountAfterAddingTax() > 0) {
            String orderId = razorpayService.createOrder(
                    booking.getFrontendFinalAmountAfterAddingTax(), 
                    booking.getReferenceId()
            );
            booking.setRazorpayOrderId(orderId);
        } else {
            booking.setRazorpayOrderId("PAID_VIA_BUNDLES"); // Skip Razorpay if 100% paid by bundles
        }

        // 8. Generate QR Code
        File qrFile;
        try {
            String qrPath = qrService.generateQRCodeWithBookingCodeRGB(refId);
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
		booking.setBookingStatus(BookingStatus.RESCHEDULED);
		booking.setReferenceId(generate("DAY_PASS_DIRECT"));
		booking.setDateOfPurchase(LocalDateTime.now());
		LocalDate today = LocalDate.now();
	     
	     if (newDate.isBefore(today)) {
	         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking date cannot be in the past");
	     }
		booking.setStartDate(newDate);
		booking.setExpiryDate(newDate);
		booking.setPrice(existing.getPrice());
		booking.setNumberOfPasses(existing.getNumberOfPasses());
		booking.setDiscountedPrice(existing.getDiscountedPrice());

		booking = dayPassBookingDirectRepository.save(booking);
//		
//		Invoice
//		Invoice invoice = new Invoice();
//		invoice.setBooking(booking);
//		invoice.setAmount(booking.getAmount());
//		invoice.setInvoiceStatus(InvoiceStatus.PAID);
//		invoice.setCompanyId(booking.getCompanyId());
//		Invoice savedInvoice = invoiceRepository.save(invoice);
//
//		String html = pdfService.buildInvoiceHtml(savedInvoice);
//		byte[] pdfBytes = pdfService.generateInvoicePdf(html);
//
//		String s3Key = s3Service.uploadInvoicePdf("letsworkcentres", savedInvoice.getCompanyId(), savedInvoice.getId(),
//				pdfBytes);
//
//		savedInvoice.setPdfS3KeyName(s3Key);
//		invoiceRepository.save(savedInvoice);
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

		if (!((booking.getBookingStatus() == BookingStatus.ACTIVE) || (booking.getBookingStatus() == BookingStatus.RESCHEDULED) ) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ACTIVE/RESCHEDULED bookings can be cancelled");
		}

		validateCancellationAllowed(booking.getStartDate());

		booking.setBookingStatus(BookingStatus.CANCELLED);

		return dayPassBookingDirectRepository.save(booking);

	}

	
}
