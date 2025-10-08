package com.LetsWork.CRM.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class CreditConferenceRoomMapping extends Base {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String conferenceRoomName;
	
	private String location;
	
	private int priceFor30Mins;
	
	private int priceFor1Hr;
	
	private int priceFor2Hrs;
	
	private int priceFor4Hrs;
	
	private String companyId;

}
