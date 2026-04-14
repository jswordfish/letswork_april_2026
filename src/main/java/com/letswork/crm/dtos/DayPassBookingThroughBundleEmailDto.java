package com.letswork.crm.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DayPassBookingThroughBundleEmailDto {
	
		private String email;
		
	    private String name;
	    
	    private String centre;
	    
	    private LocalDate dateOfUse;
	    
	    private String bookingReference;
	    
	    private String bundleReference;
	    
	    private Integer numberOfDays;
	    
	    private String qrS3Path;

}
