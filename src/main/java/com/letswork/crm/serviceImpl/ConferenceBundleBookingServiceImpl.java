package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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

import com.letswork.crm.dtos.CreateConferenceBundleBookingRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBundle;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByConferenceBundleBooking;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.ConferenceBundleBookingRepository;
import com.letswork.crm.repo.ConferenceBundleRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.ConferenceBundleBookingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ConferenceBundleBookingServiceImpl implements ConferenceBundleBookingService{
	
	
	
	private final ConferenceBundleRepository bundleRepo;
    private final ConferenceBundleBookingRepository bundleBookingRepo;
    private final LetsWorkClientRepository clientRepo;
    private final RazorpayService razorpayService;
    
    
    
    @Override
    public ConferenceBundleBooking createBundlePurchase(
    		CreateConferenceBundleBookingRequest request //
    ){

        LetsWorkClient client = clientRepo.findById(request.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found"));

        ConferenceBundle bundle = bundleRepo.findById(request.getBundleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle not found"));

        LocalDateTime now = LocalDateTime.now();   
        Date createDate = new Date(); 

        LocalDate expiryDate = now
                .plusDays(bundle.getValidForDays())
                .toLocalDate();

        ConferenceBundleBooking booking =
                ConferenceBundleBooking.builder()
                        .letsWorkClient(client)
                        .conferenceBundle(bundle)
                        .remainingHours(bundle.getNumberOfHours())
                        .price(bundle.getPrice())
                        .amount(bundle.getPrice())
                        .bookingStatus(request.getBookedFrom() == BookedFrom.APP ? BookingStatus.DRAFT : BookingStatus.ACTIVE)
                        .referenceId(generate("CONF_BUNDLE"))
                        .bookedFrom(request.getBookedFrom())
                        .frontendAmount(request.getFrontendAmount())
                        .frontendDiscountPercentage(request.getFrontendDiscountPercentage())
                        .frontendDiscountedAmount(request.getFrontendDiscountedAmount())
                        .frontendCgstPercentage(request.getFrontendCgstPercentage())
                        .frontendSgstPercentage(request.getFrontendSgstPercentage())
                        .frontendFinalAmountAfterAddingTax(request.getFrontendFinalAmountAfterAddingTax())
                        .createDate(createDate)
                        .expiryDate(expiryDate)
                        .companyId(bundle.getCompanyId())
                        .dateOfPurchase(LocalDateTime.now())
                        .build();
        
        String orderId = razorpayService.createOrder(
                booking.getFrontendFinalAmountAfterAddingTax(), 
                booking.getReferenceId()
        );

        booking.setRazorpayOrderId(orderId);
        
        ConferenceBundleBooking savedBooking = bundleBookingRepo.save(booking);
        
        
        // NOW THIS IS HANDLED IN PAYMENT VERIFICATION METHOD
//        if (client.getPurchasedConferenceCredits() == null) {
//        	Float credits = Optional.ofNullable(client.getPurchasedConferenceCredits()).orElse(0f);
//			client.setPurchasedConferenceCredits(bundle.getNumberOfHours() + credits);
//			clientRepo.save(client);
//		}else {
//			Float credits = client.getPurchasedConferenceCredits();
//			client.setPurchasedConferenceCredits(bundle.getNumberOfHours() + credits);
//			clientRepo.save(client);
//		}
        
        return savedBooking;
    }
	
    public static String generate(String prefix) {

        return prefix + "_" +
               UUID.randomUUID().toString().substring(0,8) +
               "_" +
               System.currentTimeMillis();
    }
    
    //To deduct the credits
	@Override
	public ConferenceBundleBooking deductBundleWithHours(Long bundleId, Float hours) {
		ConferenceBundleBooking booking =  bundleBookingRepo.findById(bundleId).get();
			if(booking.getRemainingHours() < hours) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough hours to book");
			}
		booking.setRemainingHours(booking.getRemainingHours() - hours);
		bundleBookingRepo.save(booking);
		return booking;
	}
	
	private PaginatedResponseDto buildResponse(Page<?> resultPage, int page, int size) {

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
	@Transactional
	public PaginatedResponseDto getBundleBookings(
	        String companyId,
	        Long clientId,
	        String referenceId,
	        BookingStatus status,
	        LocalDate fromDate,
	        LocalDate toDate,
	        Float minHours,
	        Float maxHours,
	        LocalDate expiryFrom,
	        LocalDate expiryTo,
	        
	        SortFieldByConferenceBundleBooking sortFieldConBooking,
	        SortingOrder order,
	        int page,
	        int size
	) {

		String fieldName = FIELD_MAP.get(sortFieldConBooking);
		Sort sort = order.equals(SortingOrder.DESC) ? Sort.by(fieldName).descending() : Sort.by(fieldName).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());


	    Page<ConferenceBundleBooking> result =
	    		bundleBookingRepo.filter(
	                    companyId,
	                    clientId,
	                    referenceId,
	                    status,
	                    fromDate == null ? null : fromDate.atStartOfDay(),
	                    toDate == null ? null : toDate.atTime(23, 59, 59),
	                    minHours,
	                    maxHours,
	                    expiryFrom,
	                    expiryTo,
	                    pageable
	            );

	    LocalDate today = LocalDate.now();

	    for (ConferenceBundleBooking booking : result.getContent()) {

	        if (booking.getBookingStatus() == BookingStatus.ACTIVE
	                && booking.getExpiryDate() != null
	                && booking.getExpiryDate().isBefore(today)) {

	            LetsWorkClient client = booking.getLetsWorkClient();

	            Float currentCredits = Optional
	                    .ofNullable(client.getPurchasedConferenceCredits())
	                    .orElse(0f);

	            Float remainingHours = Optional
	                    .ofNullable(booking.getRemainingHours())
	                    .orElse(0f);

	            Float updatedCredits = currentCredits - remainingHours;

	            if (updatedCredits < 0) {
	                updatedCredits = 0f; 
	            }

	            client.setPurchasedConferenceCredits(updatedCredits);

	            booking.setBookingStatus(BookingStatus.EXPIRED);

	            booking.setRemainingHours(0f);

	            clientRepo.save(client);
	            bundleBookingRepo.save(booking);
	        }
	    }

	    return buildResponse(result, page, size);
	}

	private static final Map<SortFieldByConferenceBundleBooking, String> FIELD_MAP = Map.of(

//			ID, AMOUNT, DATE_OF_PURCHASE, START_DATE, REMAINING_HOURS, EXPIRY_DATE, PRICE, DISCOUNTED_PRICE

			SortFieldByConferenceBundleBooking.ID, "id", 
			SortFieldByConferenceBundleBooking.AMOUNT,	"amount", 
			SortFieldByConferenceBundleBooking.DATE_OF_PURCHASE, "dateOfPurchase",
			SortFieldByConferenceBundleBooking.START_DATE, "startDate", 
			SortFieldByConferenceBundleBooking.REMAINING_HOURS,"remainingHours", 
			SortFieldByConferenceBundleBooking.EXPIRY_DATE, "expiryDate",
			SortFieldByConferenceBundleBooking.PRICE, "price",
			SortFieldByConferenceBundleBooking.DISCOUNTED_PRICE, "discountedPrice"
			);

}
