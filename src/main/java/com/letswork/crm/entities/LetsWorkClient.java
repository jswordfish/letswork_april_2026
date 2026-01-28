package com.letswork.crm.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

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
public class LetsWorkClient extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String clientCompanyName;
    
    private String email;
    
    private String phone;

    //private Integer totalEmployees;

    private String category;
    
    private String subCategory;
    
    private String letsWorkCentre;
    
	private String state;
	
	private String city;
	
	private String gstNumber;
	
	private String dayPassCredits;
	
	private String conferenceCredits;
	
	private String purchasedDayPassCredits;
	
	private String purchasedConferenceCredits;

}