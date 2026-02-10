package com.letswork.crm.dtos;

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

}
