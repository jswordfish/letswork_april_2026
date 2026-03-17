package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.ConferenceRoomBundleBookingRequest;
import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.repo.ConferenceBundleBookingRepository;
import com.letswork.crm.repo.ConferenceRoomBookingThroughBundleRepository;
import com.letswork.crm.repo.ConferenceRoomRepository;
import com.letswork.crm.repo.ConferenceRoomTimeSlotRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.ConferenceRoomBookingThroughBundleService;
import com.letswork.crm.service.TenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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

    @Transactional
    @Override
    public ConferenceRoomBookingThroughBundle bookUsingBundle(
            ConferenceRoomBundleBookingRequest request
    ) {
    	
		Tenant tenant = tenantService.findTenantByCompanyId(request.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+request.getCompanyId());
			
		}
		
		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(request.getCentre(), request.getCompanyId(), request.getCity(), request.getState());
		
		if(centre==null) {
			throw new RuntimeException("This LetsWorkCentre does not exists");
		}
		
		LetsWorkClient client = clientRepo.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // 1. Fetch bundle
        ConferenceBundleBooking bundle = bundleRepo.findById(request.getBundleBookingId())
                .orElseThrow(() -> new RuntimeException("Bundle not found"));

        // 2. Validate bundle
        if (bundle.getBookingStatus() != BookingStatus.ACTIVE) {
            throw new RuntimeException("Bundle is not active");
        }

        if (bundle.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Bundle expired");
        }

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
            throw new RuntimeException("Room not found");
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
                throw new RuntimeException("Slot already booked");
            }
        }

        // 5. Calculate hours
        int credits = request.getSlots().size();
        float hours = credits / 2.0f;

        if (bundle.getRemainingHours() < hours) {
            throw new RuntimeException("Not enough hours in bundle");
        }

        // 6. Create booking
        ConferenceRoomBookingThroughBundle booking =
                new ConferenceRoomBookingThroughBundle();

        booking.setBundleBooking(bundle);
        booking.setConferenceRoom(room);
        booking.setLetsWorkCentre(centre);
        booking.setLetsWorkClient(client);
        booking.setNumberOfHours(hours);
        booking.setPurchaseDate(LocalDateTime.now());
        booking.setBookingStatus(BookingStatus.ACTIVE);
        booking.setReferenceId(generate("CONF_ROOM_BUNDLE"));

        // 7. Deduct hours (IMPORTANT: inside transaction)
        bundle.setRemainingHours(bundle.getRemainingHours() - hours);
        bundleRepo.save(bundle);

        // 8. Save booking
        ConferenceRoomBookingThroughBundle savedBooking = bookingRepo.save(booking);

        // 9. Save slots
        List<ConferenceRoomTimeSlot> slots = new ArrayList<>();

        for (ConferenceRoomSlotRequest s : request.getSlots()) {
            ConferenceRoomTimeSlot t = new ConferenceRoomTimeSlot();

            t.setConferenceRoom(room);
            t.setSlotDate(request.getSlotDate());
            t.setStartTime(s.getStartTime());
            t.setEndTime(s.getEndTime());
            t.setLetsWorkCentre(centre);

            slots.add(t);
        }

        timeSlotRepo.saveAll(slots);

        return savedBooking;
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
            throw new RuntimeException("No slots selected");
        }

        slots.sort(Comparator.comparing(ConferenceRoomSlotRequest::getStartTime));

        for (int i = 0; i < slots.size(); i++) {

            ConferenceRoomSlotRequest s = slots.get(i);

            if (!s.getEndTime().equals(s.getStartTime().plusMinutes(30))) {
                throw new RuntimeException("Each slot must be 30 minutes");
            }

            if (i > 0 &&
                !slots.get(i - 1).getEndTime().equals(s.getStartTime())) {
                throw new RuntimeException("Slots must be consecutive");
            }
        }
    }
}