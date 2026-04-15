package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.DayPassBundleBookingRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.DayPassBundle;
import com.letswork.crm.entities.DayPassBundleBooking;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortField;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.DayPassBundleBookingRepository;
import com.letswork.crm.repo.DayPassBundleRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
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
	private final LetsWorkCentreRepository letsWorkCentreRepo;

	private final OffersRepository offersRepository;

	@Override
	public DayPassBundleBooking dayPassBundleBooking(DayPassBundleBookingRequest request) {
		LetsWorkClient client = clientRepo.findById(request.getClientId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found"));

		DayPassBundle bundle = dayPassBundleRepository.findById(request.getBundleId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bundle not found"));
		// 2. Centre validation
		LetsWorkCentre centre = letsWorkCentreRepo.findById(request.getLetsWorkCentreId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "LetsWorkCentre not found"));
		;

		Date createDate = new Date();

		LocalDate expiryDate = LocalDate.now().plusDays(bundle.getValidForDays());

		DayPassBundleBooking booking = DayPassBundleBooking.builder().companyId(bundle.getCompanyId())
				.dateOfPurchase(LocalDateTime.now()).letsWorkClient(client).letsWorkCentre(centre)
				.dayPassBundleeId(request.getBundleId()).price(bundle.getPrice()).amount(bundle.getPrice()).remainingNumberOfDays(bundle.getNumberOfDays())
				.bookingStatus(request.getBookedFrom() == BookedFrom.APP ? BookingStatus.DRAFT : BookingStatus.ACTIVE).referenceId(generate("DAYPASS_BUNDLE")).bookedFrom(request.getBookedFrom())
				.createDate(createDate).dateOfPurchase(LocalDateTime.now()).expiryDate(expiryDate).frontendAmount(request.getFrontendAmount())
                .frontendDiscountPercentage(request.getFrontendDiscountPercentage())
                .frontendDiscountedAmount(request.getFrontendDiscountedAmount()).build();
		booking.setStartDate(LocalDate.now());
		DayPassBundleBooking savedBooking = dayPassBundleBookingRepository.save(booking);

		return savedBooking;

	}

	public static String generate(String prefix) {

		return prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis();
	}

	@Override
	public DayPassBundleBooking deductBundleWithDays(Long bookId, Integer numOfDays) {

		DayPassBundleBooking booking = dayPassBundleBookingRepository.findById(bookId)
				.orElseThrow(() -> new RuntimeException("Bundle not found"));
		if (booking.getRemainingNumberOfDays() < numOfDays) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough days to book");
		}
		booking.setRemainingNumberOfDays(booking.getRemainingNumberOfDays() - numOfDays);

		return dayPassBundleBookingRepository.save(booking);
	}

	//

	@Override
	public PaginatedResponseDto filterDayPassBundleBooking(String companyId, BookingStatus bookingStatus, Long clientId,
			Long dayPassBundleeId, LocalDateTime date, LocalDateTime startDate, LocalDateTime endDate, Long centreId,
			LocalDateTime expiryFrom, LocalDateTime expiryTo, Integer remainingDays, Boolean paid, SortField sortField,
			SortingOrder sortDir, int page, int size) {
		// TODO Auto-generated method stub

		String fieldName = FIELD_MAP.get(sortField);
		Sort sort = sortDir.equals(SortingOrder.DESC) ? Sort.by(fieldName).descending()
				: Sort.by(fieldName).ascending();

//		Pageable pageable = PageRequest.of(page, size, Sort.by("price").descending());
		Pageable pageable = PageRequest.of(page, size, sort);
//		Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());

		Page<DayPassBundleBooking> result = dayPassBundleBookingRepository.filterDayPassBundleBooking(companyId,
				bookingStatus, clientId, dayPassBundleeId, date, startDate, endDate, centreId, remainingDays,
				expiryFrom, expiryTo, paid, pageable);

		LocalDate today = LocalDate.now();

		for (DayPassBundleBooking booking : result.getContent()) {

			if (booking.getBookingStatus() == BookingStatus.ACTIVE && booking.getExpiryDate() != null
					&& booking.getExpiryDate().isBefore(today)) {

				LetsWorkClient client = booking.getLetsWorkClient();

				Integer currentCredits = Optional.ofNullable(client.getPurchasedDayPassCredits()).orElse(0);

				Integer remaining = Optional.ofNullable(booking.getRemainingNumberOfDays()).orElse(0);

				Integer updatedCredits = currentCredits - remaining;

				if (updatedCredits < 0) {
					updatedCredits = 0;
				}

				client.setPurchasedDayPassCredits(updatedCredits);

				booking.setBookingStatus(BookingStatus.EXPIRED);

				booking.setRemainingNumberOfDays(0);

				clientRepo.save(client);
				dayPassBundleBookingRepository.save(booking);
			}
		}

		return buildResponse(result, page, size);
	}

	//
	private static final Map<SortField, String> FIELD_MAP = Map.of(
			SortField.ID,"id", 
			SortField.DISCOUNTED_PRICE, "discountedPrice",
			SortField.DATE_OF_PURCHASE, "dateOfPurchase",
			SortField.PRICE, "price", SortField.PAID, "paid", SortField.DAYPASS_BUNDLE_ID, "dayPassBundleeId",
			SortField.EXPIRY_DATE, "expiryDate", SortField.REMAINING_NUMBER_OF_DAYS, "remainingNumberOfDays");

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

	//

}
