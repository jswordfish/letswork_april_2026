package com.letswork.crm.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ConferenceBookingThroughBundleEmailDto {
	
	private String email;//
    private String name;
    private String letsworkCenter;
    private String bookingReference;
    private LocalDate dateOfBooking;
    private String startTime;
    private String endTime;
    private String qrS3Path;

}
