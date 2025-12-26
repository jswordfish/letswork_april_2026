package com.letswork.crm.entities;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.letswork.crm.enums.EnquiryType;
import com.letswork.crm.enums.Solution;

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
public class Enquiry extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String name;
	
	private String email;
	
	private String phoneNumber;
	
	private Date date;
	
	private LocalDateTime time;
	
	@Enumerated(EnumType.STRING)
	private Solution solution;
	
	private String description;
	
	private String letsWorkCentre;
	
	private String city;
	
	private String state;
	
	@Enumerated(EnumType.STRING)
	private EnquiryType enquiryType;

}
