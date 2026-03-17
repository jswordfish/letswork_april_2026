package com.letswork.crm.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.ConferenceBookingDirectRequest;
import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.Offers;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.repo.ConferenceBookingDirectRepository;
import com.letswork.crm.repo.ConferenceRoomRepository;
import com.letswork.crm.repo.ConferenceRoomTimeSlotRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.OffersRepository;
import com.letswork.crm.service.ConferenceBookingDirectService;
import com.letswork.crm.service.TenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConferenceBookingDirectServiceImpl
        implements ConferenceBookingDirectService {

    private final TenantService tenantService;
    private final LetsWorkCentreRepository letsWorkCentreRepo;
    private final LetsWorkClientRepository clientRepo;
    private final ConferenceRoomRepository roomRepo;
    private final ConferenceRoomTimeSlotRepository timeSlotRepo;
    private final ConferenceBookingDirectRepository bookingRepo;
    private final OffersRepository offersRepo;

    @Transactional
    @Override
    public ConferenceBookingDirect createDraftBooking(
            ConferenceBookingDirectRequest request
    ) {

        // 1. Tenant validation
        Tenant tenant = tenantService.findTenantByCompanyId(request.getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("Invalid companyId");
        }

        // 2. Centre validation
        LetsWorkCentre centre = letsWorkCentreRepo
                .findByNameAndCompanyIdAndCityAndState(
                        request.getCentre(),
                        request.getCompanyId(),
                        request.getCity(),
                        request.getState()
                );

        if (centre == null) {
            throw new RuntimeException("Centre not found");
        }

        // 3. Client
        LetsWorkClient client = clientRepo.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // 4. Room validation
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

        // 5. Validate slots
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

        // 6. Calculate hours
        int credits = request.getSlots().size();
        float hours = credits / 2.0f;

        // 7. Price calculation (example)
        float pricePerHour = (room.getHalfHourPrice())*2;
        float totalPrice = hours * pricePerHour;

        float discountedPrice = totalPrice;

        Offers offer = null;

        if (request.getOfferId() != null) {
            offer = offersRepo.findById(request.getOfferId())
                    .orElseThrow(() -> new RuntimeException("Invalid offer"));

            discountedPrice = applyOffer(totalPrice, offer);
        }

        // 8. Create booking
        ConferenceBookingDirect booking = new ConferenceBookingDirect();

        booking.setLetsWorkClient(client);
        booking.setLetsWorkCentre(centre);
        booking.setConferenceRoom(room);
        booking.setSlots(new ArrayList<>());
        booking.setPurchaseDate(LocalDateTime.now());
        booking.setBookingStatus(BookingStatus.DRAFT);
        booking.setReferenceId(generate("CONF_ROOM_DIRECT"));

        booking.setPrice(totalPrice);
        booking.setDiscountedPrice(discountedPrice);
        booking.setAppliedOffer(offer);

        // 9. Save booking
        ConferenceBookingDirect savedBooking = bookingRepo.save(booking);

        // 10. Save slots
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

    private float applyOffer(float price, Offers offer) {
        // Example logic
        return price - (price * offer.getDiscountPercentage() / 100);
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
