package com.LetsWork.CRM.entities;

import java.util.ArrayList;
import java.util.List;

import com.LetsWork.CRM.enums.AgreementType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandLord {
	
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
	
	private String companyId;
	
	@Enumerated(EnumType.STRING)  
    @Column(nullable = false)
    private AgreementType agreementType;
	
	@OneToMany(mappedBy = "landLord", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EscalationTimeAndPercentage> timeAndPercentage = new ArrayList<>();
	

}
