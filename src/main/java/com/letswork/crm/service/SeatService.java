package com.letswork.crm.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.BulkSeatCreationDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.dtos.SeatAvailabilityDto;
import com.letswork.crm.dtos.SeatMappingResponseDto;
import com.letswork.crm.dtos.SeatPublishRequest;
import com.letswork.crm.dtos.SeatPublishResponse;
import com.letswork.crm.entities.Seat;
import com.letswork.crm.enums.SeatType;

public interface SeatService {
	
	Seat saveOrUpdate(Seat seat);
	
	public List<Seat> bulkCreate(BulkSeatCreationDto dto);

	PaginatedResponseDto listSeats(
	        String companyId,
	        String letsWorkCentre,
	        String city,
	        String state,
	        SeatType seatType,
	        Boolean published,
	        String search,
	        String sort,
	        int pageNo,
	        int pageSize
	);
    
    PaginatedResponseDto listPublishedSeats(String companyId, String letsWorkCentre, String city, String state, int pageNo, int pageSize);

    void deleteSeat(Long id);
    
    long getTotalSeats(String companyId, String letsWorkCentre, SeatType seatType, String city, String state);
    
//    long getAvailableSeats(String companyId, String letsWorkCentre, SeatType seatType, String city, String state);
    PaginatedResponseDto getAvailableSeats(String companyId, String letsWorkCentre, SeatType seatType, String city, String state, int page);
    
    String uploadSeatExcel(MultipartFile file) throws Exception;
    
    PaginatedResponseDto findByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page);
    
    public SeatPublishResponse publishSeats(List<SeatPublishRequest> requests);
    
    public Seat unPublishSeat(Long seatId);
    
    List<SeatAvailabilityDto> getAllSeatsWithAvailability(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            LocalDate startDate,
            LocalDate endDate);
    
    public Page<SeatMappingResponseDto> getAllSeatMappings(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            int page,
            int size);
    
    public List<Seat> listSeatsInCabin(String companyId, String letsWorkCentre, String city, String state, String cabinName);

}
