package com.letswork.crm.serviceImpl;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

import com.letswork.crm.dtos.BundleBookingCreditMapper;
import com.letswork.crm.dtos.BundleUsageRequest;
import com.letswork.crm.dtos.ConferenceRoomBundleBookingRequest;
import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByConferenceThroughBundle;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.ConferenceBundleBookingRepository;
import com.letswork.crm.repo.ConferenceRoomBookingThroughBundleRepository;
import com.letswork.crm.repo.ConferenceRoomRepository;
import com.letswork.crm.repo.ConferenceRoomTimeSlotRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
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
    private final NewUserRegisterRepository newUserRegisterRepo;

    @Transactional
    @Override
    public List<ConferenceRoomBookingThroughBundle> bookUsingMultipleBundles(
            ConferenceRoomBundleBookingRequest request) {
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        
        // 1. Validations
        Tenant tenant = tenantService.findTenantByCompanyId(request.getCompanyId());
        if(tenant == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CompanyId invalid - " + request.getCompanyId());
        }
        
        LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
                request.getCentre(), request.getCompanyId(), request.getCity(), request.getState());
        if(centre == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This LetsWorkCentre does not exists");
        }
        
        LetsWorkClient client = clientRepo.findById(request.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found"));

        ConferenceRoom room = roomRepo.findByNameAndLetsWorkCentreAndCompanyIdAndCityAndState(
                request.getRoomName(), request.getCentre(), request.getCompanyId(), request.getCity(), request.getState());
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found");
        }

        validateConsecutiveSlots(request.getSlots());
        
        for (ConferenceRoomSlotRequest slot : request.getSlots()) {
            boolean exists = timeSlotRepo.existsByCompanyIdAndLetsWorkCentreAndCityAndStateAndRoomNameAndSlotDateAndStartTime(
                    request.getCompanyId(), request.getCentre(), request.getCity(), request.getState(),
                    request.getRoomName(), request.getSlotDate(), slot.getStartTime());
            if (exists) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot already booked for time: " + slot.getStartTime());
            }
        }

        float totalHoursRequired = request.getSlots().size() / 2.0f;
        float remainingRequired = totalHoursRequired;

        // 2. Initialize Single Booking
        ConferenceRoomBookingThroughBundle singleBooking = new ConferenceRoomBookingThroughBundle();
        singleBooking.setConferenceRoom(room);
        singleBooking.setLetsWorkCentre(centre);
        singleBooking.setLetsWorkClient(client);
        singleBooking.setCompanyId(request.getCompanyId());
        singleBooking.setCreateDate(new Date());
        singleBooking.setDateOfPurchase(LocalDateTime.now());
        
        LocalDate today = LocalDate.now();
        if (request.getSlotDate().isBefore(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking date cannot be in the past");
        }
        
        singleBooking.setStartDate(request.getSlotDate());
        singleBooking.setExpiryDate(request.getSlotDate());
        singleBooking.setBookingStatus(BookingStatus.ACTIVE);
        String refId = generate("CONF_ROOM_BUNDLE");
        singleBooking.setReferenceId(refId);
        singleBooking.setBookedFrom(BookedFrom.APP);
        singleBooking.setNumberOfHours(totalHoursRequired);
        
        singleBooking.setBookedByUserId(request.getBookedByUserId());
        
        NewUserRegister bookedByUser = newUserRegisterRepo.findById(request.getBookedByUserId()).orElse(null);
        
        singleBooking.setBookedByUser(bookedByUser);

        // 3. Process Bundles & Populate multipleBundleList
        List<BundleBookingCreditMapper> bundleMappers = new ArrayList<>();
        ConferenceBundleBooking primaryBundle = null; 

        for (BundleUsageRequest usage : request.getBundleUsages()) {
            if (remainingRequired <= 0) break;

            ConferenceBundleBooking bundle = bundleRepo.findById(usage.getBundleBookingId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle not found"));

            if (bundle.getBookingStatus() != BookingStatus.ACTIVE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle not active: " + bundle.getId());
            }

            if (bundle.getExpiryDate().isBefore(LocalDate.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle expired: " + bundle.getId());
            }

            float usableHours = Math.min(bundle.getRemainingHours(), usage.getHoursDeducted());
            if (usableHours <= 0) continue;

            if (usableHours > remainingRequired) {
                usableHours = remainingRequired;
            }

            // Deduct from bundle directly
            bundle.setRemainingHours(bundle.getRemainingHours() - usableHours);
            bundleRepo.save(bundle);
            
            if (primaryBundle == null) {
                primaryBundle = bundle; // Keeping the first bundle to satisfy the legacy @ManyToOne relation
            }

            // Map the usage to your new JSON mapper list
            BundleBookingCreditMapper mapper = new BundleBookingCreditMapper();
            mapper.setBundleId(bundle.getId());
            mapper.setBundleName(bundle.getConferenceBundle().getName()); // Assuming ConferenceBundleBooking has getBundleName()
            
            // Note: usableHours is float, creditsUsed is Integer. Adjust multiplier if 1 hr = 2 credits
            mapper.setCreditsUsed(Math.round(usableHours * 2)); 
            
            bundleMappers.add(mapper);
            remainingRequired -= usableHours;
        }

        if (remainingRequired > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough total hours across bundles");
        }

        // Attach data to the single booking
        singleBooking.setMultipleBundleList(bundleMappers); // This triggers your JSON serialization block
        singleBooking.setBundleBooking(primaryBundle);      // Fallback for legacy DB column

        // Deduct total hours from client once
        Float currentCredits = Optional.ofNullable(client.getPurchasedConferenceCredits()).orElse(0f);
        client.setPurchasedConferenceCredits(currentCredits - totalHoursRequired);
        clientRepo.save(client);

        // Save booking to get the ID for slot mapping
        singleBooking = bookingRepo.save(singleBooking);

        // 4. Process all Slots for this single booking
        List<ConferenceRoomTimeSlot> allSlotsToSave = new ArrayList<>();
        for (ConferenceRoomSlotRequest s : request.getSlots()) {
            ConferenceRoomTimeSlot t = new ConferenceRoomTimeSlot();
            t.setConferenceRoom(room);
            t.setSlotDate(request.getSlotDate());
            t.setStartTime(s.getStartTime());
            t.setEndTime(s.getEndTime());
            t.setLetsWorkCentre(centre);
            t.setBooking(singleBooking);
            t.setCompanyId(centre.getCompanyId());
            
            allSlotsToSave.add(t);
        }

        timeSlotRepo.saveAll(allSlotsToSave);
        singleBooking.setSlots(allSlotsToSave);
        singleBooking = bookingRepo.save(singleBooking);

        // 5. Generate One QR Code
        try {
            String qrPath = qrService.generateQRCodeWithBookingCodeRGB(refId);
            File qrFile = new File(qrPath);
            String s3Path = s3Service.uploadConferenceRoomQrCode(
                    "letsworkcentres", client.getCompanyId(), client.getEmail(), refId, qrFile);
            
            singleBooking.setQrS3Path(s3Path);
            singleBooking = bookingRepo.save(singleBooking);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "QR generation failed: " + e.getMessage());
        }

        // 6. Send One Email
        String startTime = allSlotsToSave.get(0).getStartTime().format(timeFormatter);
        String endTime = allSlotsToSave.get(allSlotsToSave.size() - 1).getEndTime().format(timeFormatter);

        mailService.sendConferenceBookingThroughBundleEmail(
                client.getEmail(),
                client.getClientCompanyName(),
                centre.getName(),
                request.getSlotDate(),
                startTime,
                endTime,
                singleBooking.getReferenceId(),
                singleBooking.getQrS3Path()
        );

        // Return as a List to satisfy the existing method signature, though it now holds exactly one item
        return Collections.singletonList(singleBooking);
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
		booking.setExpiryDate(newDate);
		
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