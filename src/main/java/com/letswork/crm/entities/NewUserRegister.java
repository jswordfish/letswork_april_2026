package com.letswork.crm.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.poiji.annotation.ExcelCellName;

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
	
//	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
	
	@ExcelCellName(value = "Name")
	private String name;
	
	@ExcelCellName(value = "Email")
	private String email;
	
	@ExcelCellName(value = "Phone Number")
	private String phoneNumber;
	
	@Temporal(TemporalType.DATE)
	@ExcelCellName(value = "Date of Birth")
	private Date dob;
	
	@ExcelCellName(value = "Profile Image Path")
	private String profileImagePath;
	
	@ExcelCellName(value = "Conference Credits")
	private Integer conferenceCredits;
	
	@ExcelCellName(value = "Day Pass")
	private Integer dayPass;
	
	@ExcelCellName(value = "Free Conference Credits")
	private Integer freeConferenceCredits;
	
	@ExcelCellName(value = "Free Day Pass")
	private Integer freeDayPass;
	
	@ExcelCellName(value = "Monthly")
	private Boolean monthly;
	
	@ExcelCellName(value = "Category")
	private String category;
	
	@ExcelCellName(value = "Sub Category")
	private String subCategory;
	
	@ExcelCellName(value = "Lets Work Centre")
	private String letsWorkCentre;
	
	@ExcelCellName(value = "City")
	private String city;
	
	@ExcelCellName(value = "State")
	private String state;
	
	private Boolean active;
	
}
