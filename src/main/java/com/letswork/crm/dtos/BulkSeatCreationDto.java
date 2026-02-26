package com.letswork.crm.dtos;

import com.letswork.crm.enums.SeatType;

import lombok.Data;

@Data
public class BulkSeatCreationDto {

    private String companyId;
    private String letsWorkCentre;
    private String city;
    private String state;

    private SeatType seatType;

    private Integer totalSeats;

    private String prefix;   

    private String cabinName;  
    
}
