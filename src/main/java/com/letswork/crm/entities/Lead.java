package com.letswork.crm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.letswork.crm.enums.LeadQuality;
import com.letswork.crm.enums.LeadStatus;
import com.letswork.crm.enums.Source;
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
@Table(name = "crm_lead")
public class Lead extends Base{
	
	@ExcelCellName("Name")
	private String name;
	
	@ExcelCellName("Email")
	private String email;
	
	@ExcelCellName("Phone")
	private String phone;
	
	@ExcelCellName("Client Company")
	private String clientCompanyName;
	
	@ExcelCellName("Source")
	@Enumerated(EnumType.STRING)
	private Source source;
	
	@ExcelCellName("Location")
	private String location;
	
	@ExcelCellName("Status")
	@Enumerated(EnumType.STRING)
	private LeadStatus status;
	
	@ExcelCellName("Lead Quality")
	@Enumerated(EnumType.STRING)
	private LeadQuality leadQuality;
	
	@ExcelCellName("LetsWork Centre")
	private String letsWorkCentre;
	
	@ExcelCellName("City")
	private String city;
	
	@ExcelCellName("State")
	private String state;
	
	@ExcelCellName("Solution")
	private String solution;
	
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String agreementJson;
	
	@ExcelCellName("Number Of Seats")
	private Integer numberOfSeats;

}
