package com.letswork.crm.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
public class DayPassBundle extends Base{
	
	@ManyToOne
	LetsWorkCentre letsWorkCentre;
	
	private Integer numberOfDays;//30
	
	private Integer validForDays;//60
	
	private Float discountPercentage;
	
	Float price;

}
