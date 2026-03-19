package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.DayPassBookingThroughBundleRequest;
import com.letswork.crm.dtos.DayPassBundleUsageRequest;
import com.letswork.crm.entities.DayPassBookingThroughBundle;
import com.letswork.crm.entities.DayPassBundleBooking;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.repo.DayPassBookingThroughBundleRepository;
import com.letswork.crm.repo.DayPassBundleBookingRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.DayPassBookingThroughBundleService;
import com.letswork.crm.service.DayPassBundleBookingService;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.TenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DayPassBookingThroughBundleImpl implements DayPassBookingThroughBundleService {

	private final DayPassBundleBookingRepository dayPassBundleBookingRepository;

	@Autowired
	TenantService tenantService;

	@Autowired
	LetsWorkCentreService letsWorkCentreService;
	
	@Autowired
	DayPassBundleBookingService dayPassBundleBookingService;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy");

	private final DayPassBookingThroughBundleRepository dayPassBookingThroughBundleRepository;
	private final LetsWorkClientRepository clientRepo;

	@Transactional
	@Override
	public List<DayPassBookingThroughBundle> dayPassBookingThroughBundleBooking(
			DayPassBookingThroughBundleRequest request) {

		Tenant tenant = tenantService.findTenantByCompanyId(request.getCompanyId());

		if (tenant == null) {

			throw new RuntimeException("CompanyId invalid - " + request.getCompanyId());

		}

		LetsWorkCentre centre = letsWorkCentreService.findById(request.getLetsworkCenterId());

		if (centre == null) {
			throw new RuntimeException("This LetsWorkCentre does not exists");
		}

		LetsWorkClient client = clientRepo.findById(request.getClientId())
				.orElseThrow(() -> new RuntimeException("Client not found"));

		List<DayPassBookingThroughBundle> bookings = new ArrayList<>();

		for (DayPassBundleUsageRequest usage : request.getBundleUsages()) {

			DayPassBundleBooking bundle = dayPassBundleBookingRepository.findById(usage.getDayPassBundleBookingId())
					.orElseThrow(() -> new RuntimeException("Bundle not found"));

			// Validate bundle
//			if (bundle.getBookingStatus() != BookingStatus.ACTIVE) {
//				throw new RuntimeException("Bundle not active: " + bundle.getId());
//			}
//
//			if (bundle.getExpiryDate().isBefore(LocalDateTime.now())) {
//				throw new RuntimeException("Bundle expired: " + bundle.getId());
//			}

			int remaining = bundle.getRemainingNumberOfDays();

			if (remaining < usage.getDaysDeducted()) {
				throw new RuntimeException("Not enough days in bundle");
			}

			bundle.setCompanyId(request.getCompanyId());
					 
			// Create booking
			DayPassBookingThroughBundle booking = new DayPassBookingThroughBundle();
			/**
			 * Bundle id is different from day pass bundle booking id
			 */
			booking.setDayPassBundleBookingId(bundle.getId());
			booking.setNumberOfDays(usage.getDaysDeducted());
 			booking.setLetsWorkClient(client);
   			booking.setBookingStatus(BookingStatus.ACTIVE);
			booking.setReferenceId(generate("DayPassBookingThroughBundle"));     
			booking.setDateOfUse(LocalDate.parse(request.getDateOfUse(), formatter));
			
			// Deduct days
			dayPassBundleBookingService.deductBundleWithDays(bundle.getId(), usage.getDaysDeducted());
			
			bookings.add(dayPassBookingThroughBundleRepository.save(booking));
		}

		return bookings;
	}
	
    public static String generate(String prefix) {

        return prefix + "_" +
               UUID.randomUUID().toString().substring(0,8) +
               "_" +
               System.currentTimeMillis();
    }

}
