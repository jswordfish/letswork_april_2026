package com.letswork.crm.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Builder
public class DayPassBookingDirectRequest {

	private Long clientId;
	private Integer numberOfPasses;
	private String companyId;
	private String centre;
	private String city;
	private String state;
	private LocalDate dateOfUse;
  
	private Long offerId;
	
	BigDecimal price;
	
 
}
