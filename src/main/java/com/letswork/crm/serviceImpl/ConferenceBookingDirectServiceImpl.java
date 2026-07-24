package com.letswork.crm.serviceImpl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
import com.letswork.crm.dtos.BundleUsageRequest;
import com.letswork.crm.dtos.ConferenceBookingDirectRequest;
import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.Offers;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByConferenceBookingDirect;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.ConferenceBookingDirectRepository;
import com.letswork.crm.repo.ConferenceBundleBookingRepository;
import com.letswork.crm.repo.ConferenceRoomRepository;
import com.letswork.crm.repo.ConferenceRoomTimeSlotRepository;
import com.letswork.crm.repo.InvoiceRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.repo.OffersRepository;
import com.letswork.crm.service.ConferenceBookingDirectService;
import com.letswork.crm.service.QRCodeService;
import com.letswork.crm.service.TenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ConferenceBookingDirectServiceImpl implements ConferenceBookingDirectService {

	private final TenantService tenantService;
	private final LetsWorkCentreRepository letsWorkCentreRepo;
	private final LetsWorkClientRepository clientRepo;
	private final ConferenceRoomRepository roomRepo;
	private final ConferenceRoomTimeSlotRepository timeSlotRepo;
	private final ConferenceBookingDirectRepository bookingRepo;
	private final OffersRepository offersRepo;
	private final QRCodeService qrService;
	private final S3Service s3Service;
	private final InvoiceRepository invoiceRepository;
    private final PdfService pdfService;
    private final RazorpayService razorpayService;
    private final ConferenceBundleBookingRepository bundleRepo;
    private final NewUserRegisterRepository newUserRegisterRepo;

    @Transactional
    @Override
    public ConferenceBookingDirect createDraftBooking(ConferenceBookingDirectRequest request) {

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

        // 4. Room validation
        ConferenceRoom room = roomRepo.findByNameAndLetsWorkCentreAndCompanyIdAndCityAndState(
                request.getRoomName(), request.getCentre(), request.getCompanyId(), request.getCity(), request.getState());
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found");
        }
        
        List<ConferenceBookingDirect> existingDrafts = bookingRepo.findExistingDrafts(client.getId(), request.getSlotDate());
        for (ConferenceBookingDirect draft : existingDrafts) {
            boolean sameSlots = draft.getSlots().stream().allMatch(existingSlot ->
                    request.getSlots().stream().anyMatch(reqSlot ->
                            reqSlot.getStartTime().equals(existingSlot.getStartTime())
                    )
            );
            if (sameSlots) {
                return draft; 
            }
        }

        // 5. Validate slots
        validateConsecutiveSlots(request.getSlots());

        for (ConferenceRoomSlotRequest slot : request.getSlots()) {
            boolean exists = timeSlotRepo.existsByCompanyIdAndLetsWorkCentreAndCityAndStateAndRoomNameAndSlotDateAndStartTime(
                    request.getCompanyId(), request.getCentre(), request.getCity(), request.getState(),
                    request.getRoomName(), request.getSlotDate(), slot.getStartTime());
            if (exists) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot already booked");
            }
        }

        // 6. Handle Bundles & Credits (Hybrid Logic)
        float totalHoursRequired = request.getSlots().size() / 2.0f;
        float hoursCoveredByBundles = 0f;
        List<BundleBookingCreditMapper> bundleMappers = new ArrayList<>();

        if (request.getBundleUsages() != null && !request.getBundleUsages().isEmpty()) {
            for (BundleUsageRequest usage : request.getBundleUsages()) {
                
                // Note: Assuming you have access to bundleRepo here
                ConferenceBundleBooking bundle = bundleRepo.findById(usage.getBundleBookingId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle not found"));

                if (bundle.getBookingStatus() != BookingStatus.ACTIVE || bundle.getExpiryDate().isBefore(LocalDate.now())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle not active or expired: " + bundle.getId());
                }

                float usableHours = Math.min(bundle.getRemainingHours(), usage.getHoursDeducted());
                if (usableHours <= 0) continue;

                // Deduct from bundle
                bundle.setRemainingHours(bundle.getRemainingHours() - usableHours);
                bundleRepo.save(bundle);
                
                hoursCoveredByBundles += usableHours;

                // Map the usage for JSON
                BundleBookingCreditMapper mapper = new BundleBookingCreditMapper();
                mapper.setBundleId(bundle.getId());
                mapper.setBundleName(bundle.getConferenceBundle().getName()); 
                mapper.setCreditsUsed(Math.round(usableHours)); // Adjust logic if 1 hr = 2 credits
                bundleMappers.add(mapper);
            }

            // Deduct from client's global balance
            Float currentCredits = Optional.ofNullable(client.getPurchasedConferenceCredits()).orElse(0f);
            client.setPurchasedConferenceCredits(currentCredits - hoursCoveredByBundles);
            clientRepo.save(client);
        }

        // 7. Pricing
        // Optional: If you want to calculate backend price based on remaining hours:
        // float billableHours = totalHoursRequired - hoursCoveredByBundles;
        
        BigDecimal pricePerHour = room.getHalfHourPrice().multiply(new BigDecimal("2"));
        BigDecimal totalPrice = pricePerHour.multiply(BigDecimal.valueOf(totalHoursRequired));
        BigDecimal discountedPrice = totalPrice;
        Offers offer = null;

        if (request.getOfferId() != null) {
            offer = offersRepo.findById(request.getOfferId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid offer"));
            discountedPrice = applyOffer(totalPrice, offer);
        }

        // 8. Create booking
        ConferenceBookingDirect booking = new ConferenceBookingDirect();
        booking.setLetsWorkClient(client);
        booking.setLetsWorkCentre(centre);
        booking.setConferenceRoom(room);
        booking.setCompanyId(centre.getCompanyId());
        booking.setBookingStatus(request.getBookedFrom() == BookedFrom.APP ? BookingStatus.DRAFT : BookingStatus.ACTIVE);
        booking.setBookedFrom(request.getBookedFrom());
        
        if(request.getBookedByUserId()!=null) {
        
        booking.setBookedByUserId(request.getBookedByUserId());
        
        NewUserRegister bookedByUser = newUserRegisterRepo.findById(request.getBookedByUserId()).orElse(null);
        
        booking.setBookedByUser(bookedByUser);
        }
        
        String refId = generate("CONF_ROOM_DIRECT");
        booking.setReferenceId(refId);
        booking.setDateOfPurchase(LocalDateTime.now());
        
        LocalDate today = LocalDate.now();
        if (request.getSlotDate().isBefore(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking date cannot be in the past");
        }
        
        booking.setStartDate(request.getSlotDate());
        booking.setExpiryDate(request.getSlotDate());
        booking.setPrice(totalPrice);
        booking.setDiscountedPrice(discountedPrice);
        booking.setAmount(discountedPrice);
        booking.setAppliedOffer(offer);
        
        // Frontend calculation fields
        booking.setFrontendAmount(request.getFrontendAmount());
        booking.setFrontendDiscountPercentage(request.getFrontendDiscountPercentage());
        booking.setFrontendDiscountedAmount(request.getFrontendDiscountedAmount());
        booking.setFrontendCgstPercentage(request.getFrontendCgstPercentage());
        booking.setFrontendSgstPercentage(request.getFrontendSgstPercentage());
        booking.setFrontendFinalAmountAfterAddingTax(request.getFrontendFinalAmountAfterAddingTax());
        
        // Attach the bundle mappings to trigger JSON serialization
        if (!bundleMappers.isEmpty()) {
            booking.setMultipleBundleList(bundleMappers);
        }

        booking.setCreateDate(new Date());

        // 🔥 Conditional Razorpay logic
        if (booking.getFrontendFinalAmountAfterAddingTax() > 0) {
            String orderId = razorpayService.createOrder(
                    booking.getFrontendFinalAmountAfterAddingTax(), 
                    booking.getReferenceId()
            );
            booking.setRazorpayOrderId(orderId);
        } else {
            // If final amount is 0, they paid 100% via bundles. No Razorpay order needed.
            booking.setRazorpayOrderId("PAID_VIA_BUNDLES");
        }

        try {
            String qrPath = qrService.generateQRCodeWithBookingCodeRGB(refId);
            File qrFile = new File(qrPath);
            String s3Path = s3Service.uploadConferenceRoomQrCode(
                    "letsworkcentres", client.getCompanyId(), client.getEmail(), refId, qrFile);
            booking.setQrS3Path(s3Path);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "QR generation failed: " + e.getMessage());
        }

        booking = bookingRepo.save(booking);

        // 9. Create & map slots
        List<ConferenceRoomTimeSlot> slots = new ArrayList<>();
        for (ConferenceRoomSlotRequest s : request.getSlots()) {
            ConferenceRoomTimeSlot t = new ConferenceRoomTimeSlot();
            t.setConferenceRoom(room);
            t.setSlotDate(request.getSlotDate());
            t.setStartTime(s.getStartTime());
            t.setEndTime(s.getEndTime());
            t.setLetsWorkCentre(centre);
            t.setCompanyId(centre.getCompanyId());
            t.setBooking(booking);
            slots.add(t);
        }

        booking.setSlots(slots);
        timeSlotRepo.saveAll(slots);
        bookingRepo.save(booking);

        return booking;
    }

	private BigDecimal applyOffer(BigDecimal price, Offers offer) {
	    // Discount amount = (price * discount) / 100
	    BigDecimal discountPercent = BigDecimal.valueOf(offer.getDiscount());
	    BigDecimal oneHundred = new BigDecimal("100");

	    BigDecimal discountAmount = price.multiply(discountPercent)
	                                     .divide(oneHundred, 2, RoundingMode.HALF_UP);

	    // Final price = price - discountAmount
	    return price.subtract(discountAmount);
	}

	public static String generate(String prefix) {

		return prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis();
	}

	private void validateConsecutiveSlots(List<ConferenceRoomSlotRequest> slots) {

		if (slots == null || slots.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No slots selected");
		}

		slots.sort(Comparator.comparing(ConferenceRoomSlotRequest::getStartTime));

		for (int i = 0; i < slots.size(); i++) {

			ConferenceRoomSlotRequest s = slots.get(i);

			if (!s.getEndTime().equals(s.getStartTime().plusMinutes(30))) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each slot must be 30 minutes");
			}

			if (i > 0 && !slots.get(i - 1).getEndTime().equals(s.getStartTime())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slots must be consecutive");
			}
		}
	}

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
	public PaginatedResponseDto getDirectBookings(String companyId, Long clientId, BookingStatus status, String centre,
			String city, String state, String roomName, LocalDate fromDate, LocalDate toDate, BigDecimal minPrice,
			BigDecimal maxPrice, SortFieldByConferenceBookingDirect sortField, SortingOrder order, int page, int size) {

		String fieldName = FIELD_MAP.get(sortField);
		Sort sort = order.equals(SortingOrder.DESC) ? Sort.by(fieldName).descending() : Sort.by(fieldName).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

//        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());

		Page<ConferenceBookingDirect> result = bookingRepo.filter(companyId, clientId, status, centre, city, state,
				roomName, fromDate == null ? null : fromDate.atStartOfDay(),
				toDate == null ? null : toDate.atTime(23, 59, 59), minPrice, maxPrice, pageable);

		return buildResponse(result, page, size);
	}

	private static final Map<SortFieldByConferenceBookingDirect, String> FIELD_MAP = Map.of(

//    		PRICE, PURCHASE_DATE, DISCOUNTED_PRICE, AMOUNT, DATE_OF_PURCHASE, START_DATE
			SortFieldByConferenceBookingDirect.ID, "id",
			SortFieldByConferenceBookingDirect.PRICE, "price", SortFieldByConferenceBookingDirect.PURCHASE_DATE,
			"purchaseDate", SortFieldByConferenceBookingDirect.DISCOUNTED_PRICE, "discountedPrice",
			SortFieldByConferenceBookingDirect.AMOUNT, "amount", SortFieldByConferenceBookingDirect.DATE_OF_PURCHASE,
			"dateOfPurchase", SortFieldByConferenceBookingDirect.START_DATE, "startDate");

	@Override
	public ConferenceBookingDirect cancel(Long id, String companyId, String source) {

		ConferenceBookingDirect booking = bookingRepo.findByIdAndCompanyId(id, companyId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking not found"));

		if (!((booking.getBookingStatus() == BookingStatus.ACTIVE) || (booking.getBookingStatus() == BookingStatus.RESCHEDULED) ) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ACTIVE/RESCHEDULED bookings can be cancelled");
		}
		
		if(source == null) {
		validateCancellationAllowed(booking.getStartDate());
		}
		//timeSlotRepo.deleteByBooking(booking);
		
		//booking.getSlots().clear();
		
		booking.setBookingStatus(BookingStatus.CANCELLED);
			for(ConferenceRoomTimeSlot slot:booking.getSlots()) {
				slot.setSoftDelete(true);
				timeSlotRepo.save(slot);
			}
		return bookingRepo.save(booking);
		

	}

	private void validateCancellationAllowed(LocalDate bookingDate) {

		LocalDate today = LocalDate.now();

		if (!today.isBefore(bookingDate)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking can only be cancelled at least one day before the booking date");
		}
	}

	@Override
	@Transactional
	public ConferenceBookingDirect reschedule(Long bookingId, LocalDate newDate,
			List<ConferenceRoomSlotRequest> newSlots, String companyId, String source) {

		ConferenceBookingDirect existing = bookingRepo.findByIdAndCompanyId(bookingId, companyId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking not found"));

		if (existing.getBookingStatus() != BookingStatus.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ACTIVE bookings can be rescheduled");
		}

//		if (existing.getStartDate().equals(newDate)) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New date must be different from current booking date");
//		}

		validateConsecutiveSlots(newSlots);

		for (ConferenceRoomSlotRequest slot : newSlots) {
			boolean exists = timeSlotRepo
					.existsByCompanyIdAndLetsWorkCentreAndCityAndStateAndRoomNameAndSlotDateAndStartTime(
							existing.getCompanyId(), existing.getLetsWorkCentre().getName(),
							existing.getLetsWorkCentre().getCity(), existing.getLetsWorkCentre().getState(),
							existing.getConferenceRoom().getName(), newDate, slot.getStartTime());

			if (exists) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot already booked");
			}
		}

		ConferenceBookingDirect booking = new ConferenceBookingDirect();

		booking.setLetsWorkClient(existing.getLetsWorkClient());
		booking.setLetsWorkCentre(existing.getLetsWorkCentre());
		booking.setConferenceRoom(existing.getConferenceRoom());
		booking.setCompanyId(existing.getCompanyId());
		booking.setBookingStatus(BookingStatus.RESCHEDULED);
		booking.setReferenceId(generate("CONF_ROOM_DIRECT"));
		booking.setDateOfPurchase(LocalDateTime.now());
		LocalDate today = LocalDate.now();
	     
	     if (newDate.isBefore(today)) {
	         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking date cannot be in the past");
	     }
	    
		booking.setStartDate(newDate);
		booking.setExpiryDate(newDate);
		booking.setPrice(existing.getPrice());
		booking.setDiscountedPrice(existing.getDiscountedPrice());

		booking = bookingRepo.save(booking);

//	    Invoice invoice = new Invoice();
//        invoice.setBooking(booking);
//        invoice.setAmount(booking.getAmount());
//        invoice.setInvoiceStatus(InvoiceStatus.PAID);
//        invoice.setCompanyId(booking.getCompanyId());
//        Invoice savedInvoice = invoiceRepository.save(invoice);
//
//        String html = pdfService.buildInvoiceHtml(savedInvoice);
//        byte[] pdfBytes = pdfService.generateInvoicePdf(html);
//        
//        String s3Key = s3Service.uploadInvoicePdf("letsworkcentres", savedInvoice.getCompanyId(), savedInvoice.getId(), pdfBytes);
//        
//        savedInvoice.setPdfS3KeyName(s3Key);
//        invoiceRepository.save(savedInvoice);
        
		List<ConferenceRoomTimeSlot> slots = new ArrayList<>();

		for (ConferenceRoomSlotRequest s : newSlots) {

			ConferenceRoomTimeSlot t = new ConferenceRoomTimeSlot();

			t.setConferenceRoom(existing.getConferenceRoom());
			t.setSlotDate(newDate);
			t.setStartTime(s.getStartTime());
			t.setEndTime(s.getEndTime());
			t.setLetsWorkCentre(existing.getLetsWorkCentre());
			t.setCompanyId(existing.getCompanyId());

			t.setBooking(booking);

			slots.add(t);
		}

		booking.setSlots(slots);
		bookingRepo.save(booking);
		timeSlotRepo.saveAll(slots);

		cancel(bookingId, companyId, source);

		return booking;
	}

}
