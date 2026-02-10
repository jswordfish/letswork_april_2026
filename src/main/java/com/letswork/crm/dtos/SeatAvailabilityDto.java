package com.letswork.crm.dtos;

import java.time.LocalDate;

import com.letswork.crm.entities.Seat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatAvailabilityDto {
	
	private Seat seat;
	
    private boolean available;
    
    private LocalDate contractStartDate;
    
    private LocalDate contractEndDate;
    
    private Long contractId;
    
    public SeatAvailabilityDto(Seat seat, boolean available) {
        this.seat = seat;
        this.available = available;
    }

}
