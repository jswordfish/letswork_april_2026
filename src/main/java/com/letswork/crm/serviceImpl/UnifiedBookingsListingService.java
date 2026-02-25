package com.letswork.crm.serviceImpl;

import java.time.LocalDate;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.UnifiedBooking;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.BookingType;
import com.letswork.crm.repo.UnifiedBookingRepository;


@Service
@Transactional
public class UnifiedBookingsListingService {
	
	@Autowired
	UnifiedBookingRepository unifiedBookingRepository;
	
	public PaginatedResponseDto getPaginated(
	        String companyId,
	        String email,
	        String letsWorkCentre,
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

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            Sort.by("createDate").descending()
	    );

	    Page<UnifiedBooking> resultPage =
	            unifiedBookingRepository.filter(
	                    companyId,
	                    email,
	                    letsWorkCentre,
	                    city,
	                    state,
	                    fromDate,
	                    toDate,
	                    roomName,
	                    status,
	                    bookingType,
	                    pageable
	            );

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
	
}
