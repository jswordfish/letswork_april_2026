package com.letswork.crm.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
public class LetsWorkClient extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String clientCompanyName;
    
    private Long userId;
    
    private String email;
    
    private String userEmail;
        
    private String phone;

    //private Integer totalEmployees;

    private String category;
    
    private String subCategory;
    
    private String letsWorkCentre;
    
	private String state;
	
	private String city;
	
	private String gstNumber;
	
	private Integer dayPass;
	
	private Integer conferenceCredits;
	
	private String purchasedDayPassCredits;
	
	private String purchasedConferenceCredits;

}