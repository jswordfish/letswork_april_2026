package com.letswork.crm.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.letswork.crm.enums.ContractStatus;

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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Contract extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lets_work_client_id", nullable = false)
	private LetsWorkClient letsWorkClient;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private Integer escalationInPercentage;
	
	private String letsworkcentre;
	
	private String city;
	
	private String state;
	
	private LocalDate commencementDate;
	
	private Integer carParkingAmount;
	
	private Integer advanceTokenAmount;
	
	@JsonFormat(pattern = "HH:mm")
	private LocalTime officeHours;
	
	private String gstNumber;
	
	private String usageOfPremises;
	
	private String cabinOffered;
	
	private Integer workstation;
	
	private Integer feesPerMonth;
	
	private Integer oneTimeSetupCharge;
	
	private String billingCycle;
	
	private String clientAdress;
	
	private String AmenitiesIncluded;
	
	private Integer noticePeriodInMonths;
	
	private Integer agreementTenureInMonths;
	
	private Integer lockInPeriodInMonths;
	
	private String depositAmountInRupees;
	
	@Enumerated(EnumType.STRING)  
    private ContractStatus contractStatus;
	
	private LocalDate actualEndDate;
	
	private String depositAmountReturnedDetails;
	
	private Boolean depositAmountReturned;
	
	private LocalDate depositAmountReturnDate;
	
	private String agreementS3KeyName;

}
