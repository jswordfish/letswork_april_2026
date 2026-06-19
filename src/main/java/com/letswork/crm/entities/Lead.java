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
	
	private String name;
	
	private String email;
	
	private String phone;
	 
	private String clientCompanyName;
	
	@Enumerated(EnumType.STRING)
	private Source source;
	
	private String location;
	
	@Enumerated(EnumType.STRING)
	private LeadStatus status;
	
	@Enumerated(EnumType.STRING)
	private LeadQuality leadQuality;
	
	private String letsWorkCentre;
	
	private String city;
	
	private String state;
	
	private String solution;
	
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String agreementJson;
	
	private Integer numberOfSeats;

}
