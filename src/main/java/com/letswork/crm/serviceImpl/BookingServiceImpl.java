package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Booking;
import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.entities.ConferenceBundle;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;
import com.letswork.crm.entities.DayPassBundle;
import com.letswork.crm.entities.DayPassBundleBooking;
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByBooking;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.BookingRepository;
import com.letswork.crm.repo.ConferenceBookingDirectRepository;
import com.letswork.crm.repo.ConferenceBundleBookingRepository;
import com.letswork.crm.repo.ConferenceRoomBookingThroughBundleRepository;
import com.letswork.crm.repo.DayPassBundleBookingRepository;
import com.letswork.crm.repo.DayPassBundleRepository;
import com.letswork.crm.repo.InvoiceRepository;
import com.letswork.crm.service.BookingService;
import com.letswork.crm.util.BookingTypeResolver;

@Service
public class BookingServiceImpl implements BookingService {

	@Autowired
	BookingRepository bookingRepo;

	@Autowired
	InvoiceRepository invoiceRepo;
	
	@Autowired
	ConferenceBundleBookingRepository confBundleRepo;
	
	@Autowired
	DayPassBundleBookingRepository dayPassBundleRepo;

	@Autowired
	private BookingTypeResolver bookingTypeResolver;
	
	@Autowired
	ConferenceBookingDirectRepository conferenceBookingRepo;
	
	@Autowired
	ConferenceRoomBookingThroughBundleRepository conferenceBookingBundleRepo;
	
	@Autowired
	DayPassBundleRepository dayPassBundleRepository;

	@Override
	public Booking save(Booking booking) {
		// TODO Auto-generated method stub
		return bookingRepo.save(booking);
	}
	
	@Transactional
	@Scheduled(fixedRate = 60000)
	public void cleanupDraftBookings() {

	    LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(15);

	    List<Booking> drafts = bookingRepo.findExpiredDrafts(expiryTime);
	    
//	    System.out.println("Drafts found: " + drafts.size());
	    
	    for (Booking booking : drafts) {
	    	
	        bookingRepo.delete(booking);
	        
	    }
	}
	
	@Override
	@Transactional
	public void deleteDraftBooking(Long bookingId) {

	    Booking booking = bookingRepo.findById(bookingId)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.BAD_REQUEST,
	                    "Booking not found with id: " + bookingId
	            ));

	    if (booking.getBookingStatus() != BookingStatus.DRAFT) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Only DRAFT bookings can be deleted"
	        );
	    }

	    if (booking instanceof ConferenceBookingDirect) {
	        ConferenceBookingDirect conferenceBooking =
	                (ConferenceBookingDirect) booking;
	        conferenceBooking.getSlots().clear();
	        bookingRepo.save(conferenceBooking);
	    }

	    if (booking instanceof ConferenceRoomBookingThroughBundle) {
	        ConferenceRoomBookingThroughBundle bundleBooking =
	                (ConferenceRoomBookingThroughBundle) booking;
	        bundleBooking.getSlots().clear();
	        bookingRepo.save(bundleBooking);
	    }

	    bookingRepo.delete(booking);
	}


	@Override
	public PaginatedResponseDto getAllBookings(
	        String companyId,
	        List<String> bookingTypes,
	        Long clientId,
	        String referenceId,
	        List<BookingStatus> status,
	        BookedFrom bookedFrom,
	        String roomName,
	        String search,
	        LocalDate fromDate,
	        LocalDate toDate,
	        LocalDate startDateFromDate,
            LocalDate startDateToDate,
	        SortFieldByBooking sortFieldByBooking,
	        SortingOrder order,
	        int page,
	        int size
	) {

	    markUsedBundles();
	    expireOldBookings();
	    expireCompletedConferenceBookings();
	    expireCompletedConferenceBookingsThroughBundle();

	    if (search != null && search.trim().isEmpty()) {
	        search = null;
	    }

	    if (bookingTypes != null && bookingTypes.isEmpty()) {
	        bookingTypes = null;
	    }

	    String fieldName = FIELD_MAP.get(sortFieldByBooking);

	    Sort sort = order == SortingOrder.DESC
	            ? Sort.by(fieldName).descending()
	            : Sort.by(fieldName).ascending();

	    Pageable pageable = PageRequest.of(page, size, sort);

	    // ✅ IMPORTANT CHANGE (List → CSV)
	    String statusCsv = (status == null || status.isEmpty())
	            ? null
	            : status.stream()
	                    .map(Enum::name)
	                    .collect(Collectors.joining(","));

	    LocalDateTime startDate = fromDate == null ? null : fromDate.atStartOfDay();
	    LocalDateTime endDate = toDate == null ? null : toDate.atTime(23, 59, 59);

	    String bookedFromStr = bookedFrom == null ? null : bookedFrom.name();

	    Page<Booking> result;

	    if (bookingTypes != null && !bookingTypes.isEmpty()) {

	        List<String> validatedTypes = bookingTypes.stream()
	                .map(type -> {
	                    Class<? extends Booking> clazz = bookingTypeResolver.resolve(type);
	                    if (clazz == null) {
	                        throw new RuntimeException("Invalid booking type: " + type);
	                    }
	                    return type;
	                })
	                .collect(Collectors.toList());

	        result = bookingRepo.filterAllBookingsWithTypes(
	                companyId,
	                validatedTypes,
	                clientId,
	                referenceId,
	                statusCsv,
	                bookedFromStr,
	                roomName,
	                search,
	                startDate,
	                endDate,
	                startDateFromDate,
	                startDateToDate,
	                pageable
	        );

	    } else {

	        result = bookingRepo.filterAllBookings(
	                companyId,
	                clientId,
	                referenceId,
	                statusCsv,
	                bookedFromStr,
	                roomName,
	                search,
	                startDate,
	                endDate,
	                startDateFromDate,
                    startDateToDate,
	                pageable
	        );
	    }

	    for (Booking booking : result.getContent()) {
	        Invoice invoice = invoiceRepo
	                .findByBookingReferenceId(booking.getReferenceId())
	                .orElse(null);
	        booking.setInvoice(invoice);
	    }

	    return buildResponse(result, page, size);
	}
	
	@Transactional
	public void expireOldBookings() {

	    LocalDate today = LocalDate.now();

	    bookingRepo.expirePastBookings(today);
	    confBundleRepo.expireConferenceBundles(today);
	    dayPassBundleRepo.expireDayPassBundles(today);
	}
	
	@Transactional
	public void markUsedBundles() {

	    confBundleRepo.markConferenceBundlesAsUsed();
	    dayPassBundleRepo.markDayPassBundlesAsUsed();
	}
	
	@Transactional
	public void expireCompletedConferenceBookings() {

	    LocalDate today = LocalDate.now();
	    LocalTime now = LocalTime.now().withSecond(0).withNano(0);

	    List<ConferenceBookingDirect> expiredBookings =
	            conferenceBookingRepo.findExpiredBookings(today, now);

	    for (ConferenceBookingDirect booking : expiredBookings) {
	        booking.setBookingStatus(BookingStatus.EXPIRED);
	    }

	    conferenceBookingRepo.saveAll(expiredBookings);
	}

	@Transactional
	public void expireCompletedConferenceBookingsThroughBundle() {

	    LocalDate today = LocalDate.now();
	    LocalTime now = LocalTime.now().withSecond(0).withNano(0);

	    List<ConferenceRoomBookingThroughBundle> expiredBookings =
	            conferenceBookingBundleRepo.findExpiredBookings(today, now);

	    for (ConferenceRoomBookingThroughBundle booking : expiredBookings) {
	        booking.setBookingStatus(BookingStatus.EXPIRED);
	    }

	    conferenceBookingBundleRepo.saveAll(expiredBookings);
	}

	private static final Map<SortFieldByBooking, String> FIELD_MAP = Map.of(SortFieldByBooking.ID, "id",
			SortFieldByBooking.AMOUNT, "amount", SortFieldByBooking.DATE_OF_PURCHASE, "date_of_purchase",
			SortFieldByBooking.START_DATE, "start_date");

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
	public void deactivateBooking(Long bookingId) {

	    Booking booking = bookingRepo.findById(bookingId)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.BAD_REQUEST,
	                    "Booking not found with id: " + bookingId
	            ));

	    if (booking.getBookingStatus() != BookingStatus.ACTIVE) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Only ACTIVE bookings can be deactivated"
	        );
	    }

	    // Validation for Conference Bundle
	    if (booking instanceof ConferenceBundleBooking) {

	        ConferenceBundleBooking conferenceBooking =
	                (ConferenceBundleBooking) booking;

	        ConferenceBundle conferenceBundle =
	                conferenceBooking.getConferenceBundle();

	        if (conferenceBundle != null &&
	                conferenceBooking.getRemainingHours() < conferenceBundle.getNumberOfHours()) {

	            throw new ResponseStatusException(
	                    HttpStatus.BAD_REQUEST,
	                    "Conference bundle cannot be deactivated because it has already been used"
	            );
	        }
	    }

	    // Validation for Day Pass Bundle
	    if (booking instanceof DayPassBundleBooking) {

	        DayPassBundleBooking dayPassBooking =
	                (DayPassBundleBooking) booking;

	        DayPassBundle dayPassBundle = dayPassBundleRepository
	                .findById(dayPassBooking.getDayPassBundleeId())
	                .orElseThrow(() -> new ResponseStatusException(
	                        HttpStatus.BAD_REQUEST,
	                        "Day pass bundle not found"
	                ));

	        if (dayPassBooking.getRemainingNumberOfDays() < dayPassBundle.getNumberOfDays()) {
	            throw new ResponseStatusException(
	                    HttpStatus.BAD_REQUEST,
	                    "Day pass bundle cannot be deactivated because it has already been used"
	            );
	        }
	    }

	    booking.setBookingStatus(BookingStatus.DEACTIVATED);
	    bookingRepo.save(booking);
	}

}
