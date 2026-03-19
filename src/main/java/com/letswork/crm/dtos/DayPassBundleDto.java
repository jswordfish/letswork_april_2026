package com.letswork.crm.dtos;

import com.letswork.crm.entities.Base;
import com.letswork.crm.entities.DayPassBundle;
import com.letswork.crm.entities.LetsWorkCentre;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DayPassBundleDto {
	
	private Integer numberOfDays;//30
	
	private Integer validForDays;//60
	
	private Float discountPercentage;
	
	Float price;
	
	Long letsWorkCentreId;
	
	

}
