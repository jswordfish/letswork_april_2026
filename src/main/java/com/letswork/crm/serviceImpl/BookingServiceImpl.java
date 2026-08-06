package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
	        List<String> roomNames, // Changed to List
	        String search,
	        List<String> letsWorkCentres, // Changed to List
	        LocalDate fromDate,
	        LocalDate toDate,
	        LocalDate startDateFromDate,
	        LocalDate startDateToDate,
	        SortFieldByBooking sortFieldByBooking,
	        SortingOrder order,
	        int page,
	        int size
	) {

	    // Execute lifecycle hooks
	    markUsedBundles();
	    expireOldBookings();
	    expireCompletedConferenceBookings();
	    expireCompletedConferenceBookingsThroughBundle();

	    // Clean up empty filter inputs
	    if (search != null && search.trim().isEmpty()) {
	        search = null;
	    }

	    if (bookingTypes != null && bookingTypes.isEmpty()) {
	        bookingTypes = null;
	    }

	    // Safe statuses
	    boolean checkStatuses = (status != null && !status.isEmpty());
	    List<BookingStatus> safeStatuses = checkStatuses 
	            ? status 
	            : List.of(BookingStatus.DRAFT);

	    // Safe Room Names
	    boolean checkRooms = (roomNames != null && !roomNames.isEmpty());
	    List<String> safeRooms = checkRooms 
	            ? roomNames 
	            : List.of(""); // Dummy value

	    // Safe Centres
	    boolean checkCentres = (letsWorkCentres != null && !letsWorkCentres.isEmpty());
	    List<String> safeCentres = checkCentres 
	            ? letsWorkCentres 
	            : List.of(""); // Dummy value

	    // Dynamic Sorting Construction
	    String fieldName = FIELD_MAP.get(sortFieldByBooking);
	    Sort sort = order == SortingOrder.DESC
	            ? Sort.by(fieldName).descending()
	            : Sort.by(fieldName).ascending();

	    Pageable pageable = PageRequest.of(page, size, sort);

	    // Convert LocalDates into accurate LocalDateTime bounds
	    LocalDateTime startDate = fromDate == null ? null : fromDate.atStartOfDay();
	    LocalDateTime endDate = toDate == null ? null : toDate.atTime(23, 59, 59);

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
	                safeStatuses,   
	                checkStatuses,  
	                bookedFrom,
	                safeRooms, // Updated
	                checkRooms, // Added flag
	                search,
	                safeCentres, // Updated
	                checkCentres, // Added flag
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
	                safeStatuses,   
	                checkStatuses,  
	                bookedFrom,
	                safeRooms, // Updated
	                checkRooms, // Added flag
	                search,
	                safeCentres, // Updated
	                checkCentres, // Added flag
	                startDate,
	                endDate,
	                startDateFromDate,
	                startDateToDate,
	                pageable
	        );
	    }

	    // Attach transient invoices
	    for (Booking booking : result.getContent()) {
	        Invoice invoice = invoiceRepo
	                .findByBookingReferenceId(booking.getReferenceId())
	                .orElse(null);
	        booking.setInvoice(invoice);
	    }

	    return buildResponse(result, page, size);
	}
	
	public static String formatWithDaySuffix(TemporalAccessor date) {
	    if (date == null) {
	        return "";
	    }
	    
	    int day = date.get(ChronoField.DAY_OF_MONTH);
	    String suffix = getDayOfMonthSuffix(day);
	    
	    // Pattern: "Mon, 20" + "th" + " Jul 2026"
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, d'" + suffix + "' MMM yyyy");
	    return formatter.format(date);
	}

	private static String getDayOfMonthSuffix(int day) {
	    if (day >= 11 && day <= 13) {
	        return "th";
	    }
	    switch (day % 10) {
	        case 1:  return "st";
	        case 2:  return "nd";
	        case 3:  return "rd";
	        default: return "th";
	    }
	}
	
	private void writeBookingRow(Row row, Booking booking) {
	    
	    row.createCell(0).setCellValue(booking.getBookingType() != null ? booking.getBookingType() : "");
	    
	    row.createCell(1).setCellValue(
	            booking.getLetsWorkClient() != null && booking.getLetsWorkClient().getClientCompanyName() != null
	                    ? booking.getLetsWorkClient().getClientCompanyName() : ""
	    );
	    row.createCell(2).setCellValue(
	    		booking.getLetsWorkClient() != null && booking.getLetsWorkClient().getEmail() != null
                ? booking.getLetsWorkClient().getEmail() : ""
	    		);
	    row.createCell(3).setCellValue(
	    		booking.getLetsWorkClient() != null && booking.getLetsWorkClient().getPhone() != null
                ? booking.getLetsWorkClient().getPhone() : ""
	    		);
	    row.createCell(4).setCellValue(
	            booking.getBookedByUser() != null && booking.getBookedByUser().getName() != null
	                    ? booking.getBookedByUser().getName() : ""
	    );
	    row.createCell(5).setCellValue(
	            booking.getLetsWorkCentre() != null && booking.getLetsWorkCentre().getName() != null
	                    ? booking.getLetsWorkCentre().getName() : ""
	    );
	    Float amount = booking.getFrontendAmount();
	    double roundedAmount = amount != null ? new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0;
	    row.createCell(6).setCellValue(roundedAmount);

	    Float amountTax = booking.getFrontendFinalAmountAfterAddingTax();
	    double roundedTax = amountTax != null ? new BigDecimal(amountTax).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0;
	    row.createCell(7).setCellValue(roundedTax);
	    row.createCell(8).setCellValue(booking.getBookedFrom() != null ? booking.getBookedFrom().toString() : "");
	    row.createCell(9).setCellValue(booking.getBookingStatus() != null ? booking.getBookingStatus().toString() : "");
	    row.createCell(10).setCellValue(formatWithDaySuffix(booking.getDateOfPurchase()));
	    row.createCell(11).setCellValue(formatWithDaySuffix(booking.getStartDate()));
	    row.createCell(12).setCellValue(booking.getExpiryDate() != null ? booking.getExpiryDate().toString() : "");
	    
	}
	
	@Override
	public void exportAllBookings(
	        String companyId,
	        List<String> bookingTypes,
	        Long clientId,
	        String referenceId,
	        List<BookingStatus> status,
	        BookedFrom bookedFrom,
	        List<String> roomNames,
	        String search,
	        List<String> letsWorkCentres,
	        LocalDate fromDate,
	        LocalDate toDate,
	        LocalDate startDateFromDate,
	        LocalDate startDateToDate,
	        SortFieldByBooking sortFieldByBooking,
	        SortingOrder order,
	        HttpServletResponse response
	) throws IOException {

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

	    boolean checkStatuses = (status != null && !status.isEmpty());
	    List<BookingStatus> safeStatuses = checkStatuses ? status : List.of(BookingStatus.DRAFT);

	    boolean checkRooms = (roomNames != null && !roomNames.isEmpty());
	    List<String> safeRooms = checkRooms ? roomNames : List.of("");

	    boolean checkCentres = (letsWorkCentres != null && !letsWorkCentres.isEmpty());
	    List<String> safeCentres = checkCentres ? letsWorkCentres : List.of("");

	    // ✅ Force sorting strictly by ID descending
	    Sort sort = Sort.by("id").descending();
	    Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);

	    LocalDateTime startDate = fromDate == null ? null : fromDate.atStartOfDay();
	    LocalDateTime endDate = toDate == null ? null : toDate.atTime(23, 59, 59);

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

	        // ✅ Pass 'pageable' instead of Pageable.unpaged()
	        result = bookingRepo.filterAllBookingsWithTypes(
	                companyId, validatedTypes, clientId, referenceId,
	                safeStatuses, checkStatuses, bookedFrom,
	                safeRooms, checkRooms, search,
	                safeCentres, checkCentres,
	                startDate, endDate, startDateFromDate, startDateToDate,
	                pageable
	        );
	    } else {
	        // ✅ Pass 'pageable' instead of Pageable.unpaged()
	        result = bookingRepo.filterAllBookings(
	                companyId, clientId, referenceId,
	                safeStatuses, checkStatuses, bookedFrom,
	                safeRooms, checkRooms, search,
	                safeCentres, checkCentres,
	                startDate, endDate, startDateFromDate, startDateToDate,
	                pageable
	        );
	    }

	    List<Booking> bookings = result.getContent();

	    for (Booking booking : bookings) {
	        Invoice invoice = invoiceRepo
	                .findByBookingReferenceId(booking.getReferenceId())
	                .orElse(null);
	        booking.setInvoice(invoice);
	    }

	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment; filename=bookings_export.xlsx");

	    String[] headers = {
	            "Booking Type", "Company Name", "Company Email", "Phone Number",
	            "Booked By", "Centre", "Amount", "Final Amount", "Booked From",
	            "Status", "Date Of Purchase", "Date Of Booking", "Expiry Date"
	    };

	    try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
	        SXSSFSheet sheet = workbook.createSheet("Bookings");
	        sheet.trackAllColumnsForAutoSizing();

	        CellStyle headerStyle = workbook.createCellStyle();
	        Font headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerStyle.setFont(headerFont);

	        Row headerRow = sheet.createRow(0);
	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	            cell.setCellStyle(headerStyle);
	        }

	        int rowIdx = 1;
	        for (Booking booking : bookings) {
	            Row row = sheet.createRow(rowIdx++);
	            writeBookingRow(row, booking);
	        }

	        for (int i = 0; i < headers.length; i++) {
	            sheet.autoSizeColumn(i);
	        }

	        workbook.write(response.getOutputStream());
	        response.flushBuffer();
	        workbook.dispose();
	    }
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

//	private static final Map<SortFieldByBooking, String> FIELD_MAP = Map.of(SortFieldByBooking.ID, "id",
//			SortFieldByBooking.AMOUNT, "amount", SortFieldByBooking.DATE_OF_PURCHASE, "date_of_purchase",
//			SortFieldByBooking.START_DATE, "start_date");
	
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
