package com.letswork.crm.serviceImpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.letswork.crm.entities.DayPassBundle;
import com.letswork.crm.entities.DayPassBundleBooking;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.repo.DayPassBundleBookingRepository;
import com.letswork.crm.repo.DayPassBundleRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.OffersRepository;
import com.letswork.crm.service.DayPassBundleBookingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DayPassBundleBookingServiceImpl implements DayPassBundleBookingService {

	private final DayPassBundleBookingRepository dayPassBundleBookingRepository;
	private final LetsWorkClientRepository clientRepo;
	private final DayPassBundleRepository dayPassBundleRepository;
	
	private final OffersRepository offersRepository;

	@Override
	public DayPassBundleBooking dayPassBundleBooking(Long clientId, Long bundleId) {
		LetsWorkClient client = clientRepo.findById(clientId)
				.orElseThrow(() -> new RuntimeException("Client not found"));

		DayPassBundle bundle = dayPassBundleRepository.findById(bundleId)
				.orElseThrow(() -> new RuntimeException("Bundle not found"));

		Date createDate = new Date();

		LocalDateTime expiryDate = LocalDateTime.now().plusDays(bundle.getValidForDays());

		DayPassBundleBooking booking = DayPassBundleBooking.builder()
				.companyId(bundle.getCompanyId())
				.letsWorkClient(client)
				.dayPassBundleeId(bundleId)
				.price(bundle.getPrice()).remainingNumberOfDays(bundle.getNumberOfDays())
				.bookingStatus(BookingStatus.DRAFT).referenceId(generate("DAYPASS_BUNDLE")).bookedFrom(BookedFrom.APP)
				.createDate(createDate).expiryDate(expiryDate).build();
		return dayPassBundleBookingRepository.save(booking);
	}

	public static String generate(String prefix) {

		return prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis();
	}

	@Override
	public DayPassBundleBooking deductBundleWithDays(Long bookId, Integer numOfDays) {
 
		DayPassBundleBooking booking = dayPassBundleBookingRepository.findById(bookId)
				.orElseThrow(() -> new RuntimeException("Bundle not found"));
		if (booking.getRemainingNumberOfDays() < numOfDays) {
			throw new RuntimeException("Not enough days to book");
		}
		booking.setRemainingNumberOfDays(booking.getRemainingNumberOfDays() - numOfDays);

		return dayPassBundleBookingRepository.save(booking);
	}

}
