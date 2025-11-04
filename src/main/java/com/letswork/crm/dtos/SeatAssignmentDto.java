package com.letswork.crm.dtos;

import com.letswork.crm.enums.SeatType;

public class SeatAssignmentDto {
	
    private SeatType seatType;
    
    private String seatNumber;

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
    
}
