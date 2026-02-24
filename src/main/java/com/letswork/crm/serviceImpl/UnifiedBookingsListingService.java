package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.dtos.UnifiedBookingDto;
import com.letswork.crm.entities.BookConferenceRoom;
import com.letswork.crm.entities.BookDayPass;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.BookingType;
import com.letswork.crm.repo.BookConferenceRoomRepository;
import com.letswork.crm.repo.BookDayPassRepository;


@Service
@Transactional
public class UnifiedBookingsListingService {
	
	@Autowired
	BookDayPassRepository dayPassRepo;
	
	@Autowired
	BookConferenceRoomRepository confRepo;
	
	public PaginatedResponseDto getUnifiedBookings(
	        String companyId,
	        String email,
	        String centre,
	        String city,
	        String state,
	        LocalDate fromDate,
	        LocalDate toDate,
	        String roomName,
	        BookingStatus status,
	        BookingType bookingType,
	        int page,
	        int size
	) {

	    List<UnifiedBookingDto> finalList = new ArrayList<>();

	    // DAY PASS
	    if (bookingType == null || bookingType == BookingType.DAY_PASS) {

	        List<BookDayPass> dayPassList =
	                dayPassRepo.filterForUnified(companyId, email, centre, city, state, fromDate, toDate);

	        finalList.addAll(
	                dayPassList.stream()
	                        .map(this::mapDayPass)
	                        .collect(Collectors.toList())
	        );
	    }

	    // CONFERENCE
	    if (bookingType == null || bookingType == BookingType.CONFERENCE_ROOM) {

	        List<BookConferenceRoom> confList =
	                confRepo.filterForUnified(companyId, email, centre, city, state, fromDate, toDate, roomName, status);

	        finalList.addAll(
	                confList.stream()
	                        .map(this::mapConference)
	                        .collect(Collectors.toList())
	        );
	    }

	    // SORT
	    finalList.sort(Comparator.comparing(UnifiedBookingDto::getDateOfBooking).reversed());

	    // PAGINATION
	    int start = page * size;
	    int end = Math.min(start + size, finalList.size());

	    List<UnifiedBookingDto> pagedList =
	            start > finalList.size() ? new ArrayList<>() : finalList.subList(start, end);

	    PaginatedResponseDto dto = new PaginatedResponseDto();
	    dto.setSelectedPage(page);
	    dto.setTotalNumberOfRecords(finalList.size());
	    dto.setTotalNumberOfPages((int) Math.ceil((double) finalList.size() / size));
	    dto.setRecordsFrom(start + 1);
	    dto.setRecordsTo(end);
	    dto.setList(pagedList);

	    return dto;
	}
	
	private UnifiedBookingDto mapDayPass(BookDayPass b) {
	    return UnifiedBookingDto.builder()
	            .bookingId(b.getId())
	            .bookingType(BookingType.DAY_PASS)
	            .email(b.getEmail())
	            .letsWorkCentre(b.getLetsWorkCentre())
	            .city(b.getCity())
	            .state(b.getState())
	            .dateOfBooking(b.getDateOfBooking())
	            .currentStatus(b.getCurrentStatus())
	            .numberOfDays(b.getNumberOfDays())
	            .build();
	}
	
	private UnifiedBookingDto mapConference(BookConferenceRoom b) {
	    return UnifiedBookingDto.builder()
	            .bookingId(b.getId())
	            .bookingType(BookingType.CONFERENCE_ROOM)
	            .email(b.getEmail())
	            .letsWorkCentre(b.getLetsWorkCentre())
	            .city(b.getCity())
	            .state(b.getState())
	            .dateOfBooking(b.getDateOfBooking())
	            .currentStatus(b.getCurrentStatus())
	            .roomName(b.getRoomName())
	            .numberOfHours(b.getNumberOfHours())
	            .build();
	}

}
