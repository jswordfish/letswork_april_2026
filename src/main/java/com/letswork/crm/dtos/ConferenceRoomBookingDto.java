package com.letswork.crm.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ConferenceRoomBookingDto {
	
	String clientEmail;
	
	String conferenceRoomName;
	
    String companyId;
    
    String letsWorkCentre;
    
    String clientCompanyName;
    
    LocalDateTime startTime;
    
    LocalDateTime endTime;
    
    String city;
    
    String state;

}
