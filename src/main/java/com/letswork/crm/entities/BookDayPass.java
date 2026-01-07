package com.letswork.crm.entities;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BookDayPass extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private LocalDate dateOfBooking;
		
	private Integer numberOfDays;
	
	private String email;
	
	private Boolean bundleUsed;
	
	private String letsWorkCentre;
	
	private String city;
	
	private String state;
	
	private String bookingCode;   
	
	private String qrS3Path;    
	
	private Boolean used;  

}
