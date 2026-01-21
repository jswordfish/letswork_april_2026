package com.letswork.crm.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
public class NewUserRegister extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String name;
	
	private String email;
	
	private String phoneNumber;
	
	@Temporal(TemporalType.DATE)
	private Date dob;
	
	private String profileImagePath;
	
	private Integer conferenceCredits;
	
	private Integer dayPass;
	
	private Integer freeConferenceCredits;
	
	private Integer freeDayPass;
	
	private Boolean monthly;
	
	private String category;
	
	private String subCategory;
	
	private String letsWorkCentre;
	
	private String city;
	
	private String state;

}
