package com.letswork.crm.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.letswork.crm.enums.CabinStatus;

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
public class Cabin extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String letsWorkCentre;
	
	private String cabinName;
	
	private String cabinNumber;
	
	private Integer totalSeats;
	
	private String description;
	
	private String state;
	
	private String city;
	
	@Enumerated(EnumType.STRING)  
    private CabinStatus cabinStatus;

}
