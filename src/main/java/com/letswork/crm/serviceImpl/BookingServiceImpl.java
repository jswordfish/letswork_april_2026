package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByBooking;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.BookingRepository;
import com.letswork.crm.repo.ConferenceBundleBookingRepository;
import com.letswork.crm.repo.DayPassBundleBookingRepository;
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
	    
	    System.out.println("Drafts found: " + drafts.size());
	    
	    for (Booking booking : drafts) {
	    	
	        bookingRepo.delete(booking);
	        
	    }
	}
	
	@Override
    @Transactional
    public void deleteDraftBooking(Long bookingId) {

        // 1️⃣ Fetch booking
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Booking not found with id: " + bookingId
                ));

        // 2️⃣ Validate status
        if (booking.getBookingStatus() != BookingStatus.DRAFT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only DRAFT bookings can be deleted"
            );
        }

        // 3️⃣ Delete booking
        bookingRepo.delete(booking);
    }


	@Override
	public PaginatedResponseDto getAllBookings(String companyId, String bookingType, Long clientId, String referenceId,
			BookingStatus status, LocalDate fromDate, LocalDate toDate, SortFieldByBooking sortFieldByBooking,
			SortingOrder order, int page, int size) {
		
		expireOldBookings();

		String fieldName = FIELD_MAP.get(sortFieldByBooking);

		Sort sort = order.equals(SortingOrder.DESC) ? Sort.by(fieldName).descending() : Sort.by(fieldName).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Class<? extends Booking> bookingClass = null;

		if (bookingType != null) {
			bookingClass = bookingTypeResolver.resolve(bookingType);

			if (bookingClass == null) {
				throw new RuntimeException("Invalid booking type: " + bookingType);
			}
		}

		Page<Booking> result = bookingRepo.filterAllBookings(companyId, bookingClass, clientId, referenceId, status,
				fromDate == null ? null : fromDate.atStartOfDay(), toDate == null ? null : toDate.atTime(23, 59, 59),
				pageable);

		for (Booking booking : result.getContent()) {

			Invoice invoice = invoiceRepo.findByBookingReferenceId(booking.getReferenceId()).orElse(null);

//	    	invoice.setBooking(null);

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

	private static final Map<SortFieldByBooking, String> FIELD_MAP = Map.of(SortFieldByBooking.ID, "id",
			SortFieldByBooking.AMOUNT, "amount", SortFieldByBooking.DATE_OF_PURCHASE, "dateOfPurchase",
			SortFieldByBooking.START_DATE, "startDate");

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

}
