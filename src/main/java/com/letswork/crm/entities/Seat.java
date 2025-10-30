package com.letswork.crm.entities;


import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.letswork.crm.enums.SeatType;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class Seat extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String letsWorkCentre;
	
	
	
	@Enumerated(EnumType.STRING)
	private SeatType seatType;
	
	private String seatNumber;
	
	private int costPerDay;
	
	private int costPerMonth;
	
	private String cabinName;
	
	@Builder.Default
	private Boolean published = false;
	
	private String state;
	
	private String city;

}
