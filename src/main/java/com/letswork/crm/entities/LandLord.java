package com.LetsWork.CRM.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.LetsWork.CRM.enums.AgreementType;

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
public class LandLord extends Base{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String spocFirstName;
	
	private String spocLastName;
	
	private String spocEmail;
	
	@Column(unique = true, nullable = false)
	private String gstNumber;
	
	private String gstCertificateS3Path;
	
	private String aadharNumber;
	
	private String aadharCardS3Path;
	
	private String spocAadharNumber;
	
	private String spocAadharCardS3Path;
	
	private String panNumber;
	
	private String panCardS3Path;
	
	private String spocPanNumber;
	
	private String spocPanCardS3Path;
	
	private String tinNumber;
	
	private String cinNumber;
	
	private String depositAmount;
	
	private String tenure;
	
	private String rent;
	
	private String remarks;
	
	private String agreementFileS3Path;
	

	
	@Enumerated(EnumType.STRING)  
    @Column(nullable = false)
    private AgreementType agreementType;
	
	@OneToMany(mappedBy = "landLord", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EscalationTimeAndPercentage> timeAndPercentage = new ArrayList<>();
	

}
