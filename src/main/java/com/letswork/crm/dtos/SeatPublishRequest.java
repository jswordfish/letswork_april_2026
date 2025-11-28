package com.letswork.crm.dtos;

import com.letswork.crm.enums.SeatType;

import lombok.Data;

@Data
public class SeatPublishRequest {
	
	private String letsWorkCentre;
    private String companyId;
    private String city;
    private String state;
    private SeatType seatType;
    private String seatNumber;

}
