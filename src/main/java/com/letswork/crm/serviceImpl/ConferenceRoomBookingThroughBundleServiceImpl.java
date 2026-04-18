package com.letswork.crm.serviceImpl;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.BundleUsageRequest;
import com.letswork.crm.dtos.ConferenceBookingThroughBundleEmailDto;
import com.letswork.crm.dtos.ConferenceRoomBundleBookingRequest;
import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByConferenceThroughBundle;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.ConferenceBundleBookingRepository;
import com.letswork.crm.repo.ConferenceRoomBookingThroughBundleRepository;
import com.letswork.crm.repo.ConferenceRoomRepository;
import com.letswork.crm.repo.ConferenceRoomTimeSlotRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.ConferenceRoomBookingThroughBundleService;
import com.letswork.crm.service.QRCodeService;
import com.letswork.crm.service.TenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ConferenceRoomBookingThroughBundleServiceImpl
        implements ConferenceRoomBookingThroughBundleService {
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;

    private final ConferenceBundleBookingRepository bundleRepo;
    private final ConferenceRoomBookingThroughBundleRepository bookingRepo;
    private final ConferenceRoomRepository roomRepo;
    private final ConferenceRoomTimeSlotRepository timeSlotRepo;
    private final LetsWorkClientRepository clientRepo;
    private final MailJetOtpService mailService;
    private final QRCodeService qrService;
    private final S3Service s3Service;

    @Transactional
    @Override
    public List<ConferenceRoomBookingThroughBundle> bookUsingMultipleBundles(
            ConferenceRoomBundleBookingRequest request
    		) {
    	
		Tenant tenant = tenantService.findTenantByCompanyId(request.getCompanyId());
		
		if(tenant==null) {
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CompanyId invalid - "+request.getCompanyId());
			
		}
		
		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(request.getCentre(), request.getCompanyId(), request.getCity(), request.getState());
		
		if(centre==null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This LetsWorkCentre does not exists");
		}
		
		LetsWorkClient client = clientRepo.findById(request.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found"));

        // 3. Validate room
        ConferenceRoom room = roomRepo
                .findByNameAndLetsWorkCentreAndCompanyIdAndCityAndState(
                        request.getRoomName(),
                        request.getCentre(),
                        request.getCompanyId(),
                        request.getCity(),
                        request.getState()
                );

        if (room == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found");
        }

        // 4. Validate slots
        validateConsecutiveSlots(request.getSlots());
        
        for (ConferenceRoomSlotRequest slot : request.getSlots()) {

            boolean exists = timeSlotRepo
                    .existsByCompanyIdAndLetsWorkCentreAndCityAndStateAndRoomNameAndSlotDateAndStartTime(
                            request.getCompanyId(),
                            request.getCentre(),
                            request.getCity(),
                            request.getState(),
                            request.getRoomName(),
                            request.getSlotDate(),
                            slot.getStartTime()
                    );

            if (exists) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Slot already booked for time: " + slot.getStartTime()
                );
            }
        }

        float totalHoursRequired = request.getSlots().size() / 2.0f;

        float remainingRequired = totalHoursRequired;

        List<ConferenceRoomBookingThroughBundle> bookings = new ArrayList<>();
        List<ConferenceRoomTimeSlot> allSlotsToSave = new ArrayList<>();

        List<ConferenceRoomSlotRequest> slotQueue = new ArrayList<>(request.getSlots());
        List<ConferenceBookingThroughBundleEmailDto> emailQueue = new ArrayList<>();

        for (BundleUsageRequest usage : request.getBundleUsages()) {

            if (remainingRequired <= 0) break;

            ConferenceBundleBooking bundle = bundleRepo.findById(usage.getBookingId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle not found"));

            if (bundle.getBookingStatus() != BookingStatus.ACTIVE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle not active: " + bundle.getId());
            }

            if (bundle.getExpiryDate().isBefore(LocalDate.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle expired: " + bundle.getId());
            }

            float usableHours = Math.min(
                    bundle.getRemainingHours(),
                    usage.getHoursDeducted()
            );

            if (usableHours <= 0) continue;

            if (usableHours > remainingRequired) {
                usableHours = remainingRequired;
            }

            // ✅ Create booking
            ConferenceRoomBookingThroughBundle booking =
                    new ConferenceRoomBookingThroughBundle();

            booking.setBundleBooking(bundle);
            booking.setConferenceRoom(room);
            booking.setLetsWorkCentre(centre);
            booking.setLetsWorkClient(client);
            booking.setNumberOfHours(usableHours);
            booking.setCompanyId(bundle.getCompanyId());
            booking.setDateOfPurchase(LocalDateTime.now());
            LocalDate today = LocalDate.now();
   	     
	   	     if (request.getSlotDate().isBefore(today)) {
	   	         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking date cannot be in the past");
	   	     }
            booking.setStartDate(request.getSlotDate());
            booking.setBookingStatus(BookingStatus.ACTIVE);
            String refId = generate("CONF_ROOM_BUNDLE");
            booking.setReferenceId(refId);

            booking = bookingRepo.save(booking);
            
            Float currentCredits = Optional
                    .ofNullable(client.getPurchasedConferenceCredits())
                    .orElse(0f);

            Float hoursToDeduct = Optional
                    .ofNullable(booking.getNumberOfHours())
                    .orElse(0f);

            client.setPurchasedConferenceCredits(currentCredits - hoursToDeduct);
            
            clientRepo.save(client);

            // ✅ FIXED: safer slot calculation
            int slotsNeeded = Math.round(usableHours * 2);

            List<ConferenceRoomTimeSlot> bookingSlots = new ArrayList<>();

            for (int i = 0; i < slotsNeeded; i++) {

                if (slotQueue.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough slots provided");
                }

                ConferenceRoomSlotRequest s = slotQueue.remove(0);

                ConferenceRoomTimeSlot t = new ConferenceRoomTimeSlot();
                t.setConferenceRoom(room);
                t.setSlotDate(request.getSlotDate());
                t.setStartTime(s.getStartTime());
                t.setEndTime(s.getEndTime());
                t.setLetsWorkCentre(centre);
                t.setBooking(booking);
                t.setCompanyId(centre.getCompanyId());


                bookingSlots.add(t);
            }

            booking.setSlots(bookingSlots);
            bookingRepo.save(booking);

            allSlotsToSave.addAll(bookingSlots);

            bundle.setRemainingHours(bundle.getRemainingHours() - usableHours);
            bundleRepo.save(bundle);
            
            try {
                String qrPath = qrService.generateQRCodeWithBookingCodeRGB(
                		refId
                );

                File qrFile = new File(qrPath);

                String s3Path = s3Service.uploadConferenceRoomQrCode(
                        "letsworkcentres",
                        client.getCompanyId(),
                        client.getEmail(),
                        refId,
                        qrFile
                );

                booking.setQrS3Path(s3Path);
                

            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "QR generation failed: " + e.getMessage());
            }
            
            bookings.add(booking);

            // Track remaining requirement
            remainingRequired -= usableHours;
            
         // Calculate start & end time from slots
            String startTime = bookingSlots.get(0).getStartTime().toString();
            String endTime = bookingSlots.get(bookingSlots.size() - 1).getEndTime().toString();

            // Prepare email DTO
            ConferenceBookingThroughBundleEmailDto dto = new ConferenceBookingThroughBundleEmailDto();
            dto.setEmail(client.getEmail());
            dto.setName(client.getClientCompanyName());
            dto.setLetsworkCenter(centre.getName());
            dto.setBookingReference(booking.getReferenceId());
            dto.setDateOfBooking(request.getSlotDate());
            dto.setStartTime(startTime);
            dto.setEndTime(endTime);
            dto.setQrS3Path(booking.getQrS3Path());

            emailQueue.add(dto);
            
        }

        // Final validation
        if (remainingRequired > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough total hours across bundles");
        }

        // Save all slots in batch
        timeSlotRepo.saveAll(allSlotsToSave);
        
        for (ConferenceBookingThroughBundleEmailDto dto : emailQueue) {
            mailService.sendConferenceBookingThroughBundleEmail(
                    dto.getEmail(),
                    dto.getName(),
                    dto.getLetsworkCenter(),
                    dto.getDateOfBooking(),
                    dto.getStartTime(),
                    dto.getEndTime(),
                    dto.getBookingReference(),
                    dto.getQrS3Path()
            );
        }

        return bookings;
    }
    
    public static String generate(String prefix) {

        return prefix + "_" +
               UUID.randomUUID().toString().substring(0,8) +
               "_" +
               System.currentTimeMillis();
    }
    
    private void validateConsecutiveSlots(
            List<ConferenceRoomSlotRequest> slots
    ) {

        if (slots == null || slots.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No slots selected");
        }

        slots.sort(Comparator.comparing(ConferenceRoomSlotRequest::getStartTime));

        for (int i = 0; i < slots.size(); i++) {

            ConferenceRoomSlotRequest s = slots.get(i);

            if (!s.getEndTime().equals(s.getStartTime().plusMinutes(30))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each slot must be 30 minutes");
            }

            if (i > 0 &&
                !slots.get(i - 1).getEndTime().equals(s.getStartTime())) {
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
        dto.setRecordsTo(
                Math.min((page + 1) * size, (int) resultPage.getTotalElements())
        );
        dto.setList(resultPage.getContent());

        return dto;
    }
    
    @Override
    public PaginatedResponseDto getThroughBundleBookings(
            String companyId,
            Long clientId,
            BookingStatus status,
            String centre,
            String city,
            String state,
            String roomName,
            LocalDate fromDate,
            LocalDate toDate,
            Float minHours,
            Float maxHours,
            SortFieldByConferenceThroughBundle throughBundle,
            SortingOrder order,
            int page,
            int size
    ) {


    	String fieldName = FIELD_MAP.get(throughBundle);
		Sort sort = order.equals(SortingOrder.DESC) ? Sort.by(fieldName).descending() : Sort.by(fieldName).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());

        Page<ConferenceRoomBookingThroughBundle> result =
        		bookingRepo.filter(
                        companyId,
                        clientId,
                        status,
                        centre,
                        city,
                        state,
                        roomName,
                        fromDate == null ? null : fromDate.atStartOfDay(),
                        toDate == null ? null : toDate.atTime(23, 59, 59),
                        minHours,
                        maxHours,
                        pageable
                );

        return buildResponse(result, page, size);
    }
    
    private static final Map<SortFieldByConferenceThroughBundle, String> FIELD_MAP = Map.of(
 			
			SortFieldByConferenceThroughBundle.ID, "id",
			SortFieldByConferenceThroughBundle.AMOUNT, "amount",
			SortFieldByConferenceThroughBundle.DATE_OF_PURCHASE, "dateOfPurchase",
			SortFieldByConferenceThroughBundle.START_DATE,"startDate",
			SortFieldByConferenceThroughBundle.NUMBER_OF_GUESTS ,"numberOfGuests",
			SortFieldByConferenceThroughBundle.NUMBER_OF_HOURS,"numberOfHours"
		);
    
    
    @Override
	public ConferenceRoomBookingThroughBundle cancel(Long id, String companyId) {

    	ConferenceRoomBookingThroughBundle booking = bookingRepo.findByIdAndCompanyId(id, companyId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking not found"));

		//if (  !( (booking.getBookingStatus().equals(BookingStatus.ACTIVE.toString()) || (booking.getBookingStatus().equals(BookingStatus.RESCHEDULED.toString()) )){
    	if (!((booking.getBookingStatus() == BookingStatus.ACTIVE) || (booking.getBookingStatus() == BookingStatus.RESCHEDULED) ) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ACTIVE/RESCHEDULED bookings can be cancelled");
		}

		validateCancellationAllowed(booking.getStartDate());

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
	public ConferenceRoomBookingThroughBundle reschedule(Long bookingId, LocalDate newDate,
			List<ConferenceRoomSlotRequest> newSlots, String companyId) {

		ConferenceRoomBookingThroughBundle existing = bookingRepo.findByIdAndCompanyId(bookingId, companyId)
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

		ConferenceRoomBookingThroughBundle booking = new ConferenceRoomBookingThroughBundle();

		booking.setLetsWorkClient(existing.getLetsWorkClient());
		booking.setLetsWorkCentre(existing.getLetsWorkCentre());
		booking.setConferenceRoom(existing.getConferenceRoom());
		booking.setBundleBooking(existing.getBundleBooking());
		booking.setCompanyId(existing.getCompanyId());
		booking.setNumberOfHours(existing.getNumberOfHours());
		booking.setBookingStatus(BookingStatus.RESCHEDULED);
		String refId = generate("CONF_ROOM_DIRECT");
		booking.setReferenceId(refId);
		booking.setPreviousBookingId(existing.getId());
		booking.setDateOfPurchase(LocalDateTime.now());
		LocalDate today = LocalDate.now();
	     
	     if (newDate.isBefore(today)) {
	         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking date cannot be in the past");
	     }
		booking.setStartDate(newDate);
		
		try {
            String qrPath = qrService.generateQRCodeWithBookingCodeRGB(
            		refId
            );

            File qrFile = new File(qrPath);

            String s3Path = s3Service.uploadConferenceRoomQrCode(
                    "letsworkcentres",
                    booking.getCompanyId(),
                    booking.getLetsWorkClient().getEmail(),
                    refId,
                    qrFile
            );

            booking.setQrS3Path(s3Path);
            

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "QR generation failed: " + e.getMessage());
        }
		

		booking = bookingRepo.save(booking);

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

		cancel(bookingId, companyId);

		return booking;
	}
    
}