package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.letswork.crm.entities.ConferenceBundle;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.repo.BookingRepository;
import com.letswork.crm.repo.ConferenceBundleBookingRepository;
import com.letswork.crm.repo.ConferenceBundleRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.ConferenceBundleBookingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConferenceBundleBookingServiceImpl implements ConferenceBundleBookingService{
	
	
	
	private final ConferenceBundleRepository bundleRepo;
    private final ConferenceBundleBookingRepository bundleBookingRepo;
    private final LetsWorkClientRepository clientRepo;
    
    private final ConferenceBundleBookingRepository conferenceBundleBookingRepository;
    
    
    @Override
    public ConferenceBundleBooking createBundlePurchase(
            Long clientId,
            Long bundleId
    ){

        LetsWorkClient client = clientRepo.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        ConferenceBundle bundle = bundleRepo.findById(bundleId)
                .orElseThrow(() -> new RuntimeException("Bundle not found"));

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
                        .bookingStatus(BookingStatus.DRAFT)
                        .referenceId(generate("CONF_BUNDLE"))
                        .bookedFrom(BookedFrom.APP)
                        .createDate(createDate)
                        .expiryDate(expiryDate)
                        .build();

        return bundleBookingRepo.save(booking);
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
		ConferenceBundleBooking booking =  conferenceBundleBookingRepository.findById(bundleId).get();
			if(booking.getRemainingHours() < hours) {
				throw new RuntimeException("Not enough hours to book");
			}
		booking.setRemainingHours(booking.getRemainingHours() - hours);
		conferenceBundleBookingRepository.save(booking);
		return booking;
	}


}
