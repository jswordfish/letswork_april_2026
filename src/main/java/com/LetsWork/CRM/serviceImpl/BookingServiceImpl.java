package com.LetsWork.CRM.serviceImpl;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.LetsWork.CRM.dtos.BookingValidationResponse;
import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.Booking;
import com.LetsWork.CRM.entities.Client;
import com.LetsWork.CRM.entities.ClientCompany;
import com.LetsWork.CRM.entities.ConferenceRoom;
import com.LetsWork.CRM.entities.CreditConferenceRoomMapping;
import com.LetsWork.CRM.entities.UserCreditTransactionLog;
import com.LetsWork.CRM.enums.BookingStatus;
import com.LetsWork.CRM.enums.CreditTransactionType;
import com.LetsWork.CRM.repo.BookingRepository;
import com.LetsWork.CRM.repo.ClientCompanyRepository;
import com.LetsWork.CRM.repo.ClientRepository;
import com.LetsWork.CRM.repo.ConferenceRoomRepository;
import com.LetsWork.CRM.repo.CreditConferenceRoomMappingRepository;
import com.LetsWork.CRM.service.BookingService;
import com.LetsWork.CRM.service.CreditConferenceRoomMappingService;
import com.LetsWork.CRM.service.QRCodeService;
import com.LetsWork.CRM.service.UserCreditTransactionLogService;
import com.LetsWork.CRM.service.WhatsAppService;
import com.LetsWork.CRM.util.InsufficientCreditsException;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {
	
	@Autowired
    BookingRepository bookingRepository;
	
	@Autowired
    ClientRepository clientRepository;
	
	@Autowired
    ClientCompanyRepository clientCompanyRepository;
	
	@Autowired
    ConferenceRoomRepository conferenceRoomRepository;
	
	@Autowired
    QRCodeService qrCodeService;
	
	@Autowired
	WhatsAppService whatsAppService;
	
	@Autowired
    private CreditConferenceRoomMappingService mappingService;
    
    @Autowired
    private UserCreditTransactionLogService transactionService;
    
    @Autowired
    CreditConferenceRoomMappingRepository mappingRepository;
	
	@Autowired
	S3Service s3Service;

	@Override
	public Booking createBooking(String clientName, String clientEmail, String conferenceRoomName,
	                             String companyId, String location, String clientCompanyName,
	                             LocalDateTime startTime, LocalDateTime endTime) throws Exception {

	    // 1. Validate client
	    Client client = clientRepository.findByNameAndEmailAndCompanyId(clientName, clientEmail, companyId);
	    if (client == null) throw new IllegalArgumentException("Client not found with provided details.");

	    // 2. Validate client company
	    ClientCompany clientCompany = clientCompanyRepository.findByClientCompanyNameAndLocationAndCompanyId(
	            clientCompanyName, location, companyId);
	    if (clientCompany == null) throw new IllegalArgumentException("Client company not found with provided details.");

	    // 3. Validate conference room
	    ConferenceRoom room = conferenceRoomRepository.findByNameAndLocationAndCompanyId(
	            conferenceRoomName, location, companyId);
	    if (room == null) throw new IllegalArgumentException("Conference room not found with provided details.");

	    // 4. Validate time range
	    if (!endTime.isAfter(startTime))
	        throw new IllegalArgumentException("End time must be after start time.");

	    long durationMinutes = Duration.between(startTime, endTime).toMinutes();
	    if (durationMinutes < 30)
	        throw new IllegalArgumentException("Minimum booking duration is 30 minutes.");

	    // 5. Prevent past bookings
	    if (startTime.isBefore(LocalDateTime.now()))
	        throw new IllegalArgumentException("Start time cannot be in the past.");

	    // 6. Prevent booking too far in future (max 3 months)
	    if (startTime.isAfter(LocalDateTime.now().plusMonths(3)))
	        throw new IllegalArgumentException("You cannot book a room more than 3 months in advance.");

	    // 7. Prevent booking outside allowed hours
	    LocalTime startLimit = LocalTime.of(7, 0);
	    LocalTime endLimit = LocalTime.of(23, 0);
	    if (startTime.toLocalTime().isBefore(startLimit) || endTime.toLocalTime().isAfter(endLimit)) {
	        throw new IllegalArgumentException("Bookings are only allowed between 7 AM and 11 PM.");
	    }

	    // 8. Check for booking conflicts
	    List<Booking> conflicts = bookingRepository.findConflictingBookings(
	            conferenceRoomName, location, companyId, startTime, endTime);
	    if (!conflicts.isEmpty()) {
	        throw new IllegalArgumentException("Booking conflict: Conference room is already booked in this slot.");
	    }

	    // 9. Calculate credits
	    int requiredCredits = calculateCreditCost(conferenceRoomName, location, companyId, durationMinutes);

	    // 10. Debit credits
	    try {
	        UserCreditTransactionLog debitTransaction = UserCreditTransactionLog.builder()
	                .userEmail(clientEmail)
	                .companyId(companyId)
	                .totalCredits(requiredCredits)
	                .creditTransactionType(CreditTransactionType.debit)
	                .creditsUsedOn(conferenceRoomName + " booking from " + startTime + " to " + endTime)
	                .build();

	        transactionService.logAndProcessTransaction(debitTransaction);
	    } catch (InsufficientCreditsException e) {
	        throw new Exception("Credit debit failed: " + e.getMessage());
	    }

	    // 11. Generate QR & upload
	    String bookingCode = UUID.randomUUID().toString();
	    String qrPath = qrCodeService.generateQRCodeWithBookingCodeRGB(bookingCode);
//      whatsAppService.sendBookingQRCode(client2.getPhone(), qrPath);
	    File file = new File(qrPath);
	    String s3Path = s3Service.uploadQRCode("myapp-bucket-1758037822620", companyId, clientEmail,
	            startTime + "To" + endTime, file);

	    // 12. Save booking
	    Booking booking = Booking.builder()
	            .clientName(clientName)
	            .clientEmail(clientEmail)
	            .clientCompany(clientCompanyName)
	            .conferenceRoomName(conferenceRoomName)
	            .companyId(companyId)
	            .location(location)
	            .startTime(startTime)
	            .endTime(endTime)
	            .bookingCode(bookingCode)
	            .qrCodePath(qrPath)
	            .s3Path(s3Path)
	            .currentStatus(BookingStatus.ACTIVE)
	            .isActive(true)
	            .build();

	    return bookingRepository.save(booking);
	}
    
private int calculateCreditCost(String roomName, String location, String companyId, long durationMinutes) {
        
        
        CreditConferenceRoomMapping mapping = mappingRepository.findByConferenceRoomNameAndLocationAndCompanyId(roomName, location, companyId);
        
        if (mapping == null) {
            
            throw new IllegalStateException("Credit mapping not found for conference room: " + roomName);
        }

        int totalCredits = 0;
        long remainingMinutes = durationMinutes;

        
        int priceFor4Hrs = mapping.getPriceFor4Hrs();
        int priceFor2Hrs = mapping.getPriceFor2Hrs();
        int priceFor1Hr = mapping.getPriceFor1Hr();
        int priceFor30Mins = mapping.getPriceFor30Mins();

        
        
        
        while (remainingMinutes >= 240) {
            totalCredits += priceFor4Hrs;
            remainingMinutes -= 240;
        }

        
        while (remainingMinutes >= 120) {
            totalCredits += priceFor2Hrs;
            remainingMinutes -= 120;
        }

        
        while (remainingMinutes >= 60) {
            totalCredits += priceFor1Hr;
            remainingMinutes -= 60;
        }
        
        
        if (remainingMinutes > 0) {
             
             totalCredits += priceFor30Mins;
        }

        return totalCredits;
    }


@Override
public String cancelBooking(String bookingCode) {
    return bookingRepository.findByBookingCode(bookingCode)
            .map(booking -> {
                if (booking.getStartTime().isBefore(LocalDateTime.now().plusHours(5))) {
                    return "Cannot cancel within 5 hours of booking start time.";
                }

                booking.setCurrentStatus(BookingStatus.CANCELLED);
                booking.setActive(false);
                bookingRepository.save(booking);

                // Refund credits
                int durationMinutes = (int) Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes();
                int refundCredits = calculateCreditCost(
                        booking.getConferenceRoomName(),
                        booking.getLocation(),
                        booking.getCompanyId(),
                        durationMinutes
                );

                UserCreditTransactionLog refundTransaction = UserCreditTransactionLog.builder()
                        .userEmail(booking.getClientEmail())
                        .companyId(booking.getCompanyId())
                        .totalCredits(refundCredits)
                        .creditTransactionType(CreditTransactionType.credit)
                        .creditsUsedOn("Refund for cancelled booking: " + booking.getConferenceRoomName())
                        .build();

                try {
					transactionService.logAndProcessTransaction(refundTransaction);
				} catch (InsufficientCreditsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                return "Booking cancelled successfully and credits refunded.";
            })
            .orElse("Booking not found for code: " + bookingCode);
}

    @Override
    public BookingValidationResponse validateBooking(String bookingCode) {
        Optional<Booking> bookingOpt = bookingRepository.findByBookingCode(bookingCode);

        if (bookingOpt.isEmpty()) {
            return new BookingValidationResponse(false, "Booking does not exist.");
        }

        Booking booking = bookingOpt.get();
        LocalDateTime now = LocalDateTime.now();

        if (booking.getCurrentStatus() == BookingStatus.CANCELLED) {
            return new BookingValidationResponse(false, "This booking has been cancelled.");
        }

        if (now.isBefore(booking.getStartTime())) {
            return new BookingValidationResponse(false, "You are early. Booking starts at: " + booking.getStartTime());
        }

        if (now.isAfter(booking.getEndTime())) {
            return new BookingValidationResponse(false, "This booking has expired. End time was: " + booking.getEndTime());
        }

        return new BookingValidationResponse(true, "Booking is valid. Access granted.");
    }
    
    @Override
    public PaginatedResponseDto listAllBookings(String companyId, int page, int size) {
        // Ensure page index is not negative
        Pageable pageable = PageRequest.of(Math.max(0, page), size);
        
        // Assuming your BookingRepository has a method findByCompanyId(String, Pageable)
        Page<Booking> bookingPage = bookingRepository.findByCompanyId(companyId, pageable);
        
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setList(bookingPage.getContent());
        response.setSelectedPage(bookingPage.getNumber());
        response.setTotalNumberOfRecords((int) bookingPage.getTotalElements());
        response.setTotalNumberOfPages(bookingPage.getTotalPages());
        // Calculate recordsFrom and recordsTo based on the actual page content
        response.setRecordsFrom(bookingPage.getNumberOfElements() > 0 ? (page * size) : 0);
        response.setRecordsTo(bookingPage.getNumberOfElements() > 0 ? (response.getRecordsFrom() + bookingPage.getNumberOfElements()) : 0);
        
        return response;
    }
}
