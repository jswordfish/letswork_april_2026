package com.letswork.crm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Table(
	    name = "cabin",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_cabin_name_number_centre_company_id", 
	            columnNames = {"cabin_name", "cabin_number", "lets_work_centre", "company_id"}
	        )
	    }
	)
public class Cabin extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "lets_work_centre")
	private String letsWorkCentre;
	
	@Column(name = "cabin_name")
	private String cabinName;
	
	@Column(name = "cabin_number")
	private String cabinNumber;
	
	private Integer totalSeats;
	
	private String description;
	
	private String state;
	
	private String city;
	
	@Enumerated(EnumType.STRING)  
    private CabinStatus cabinStatus;

}
