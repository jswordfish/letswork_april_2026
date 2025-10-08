package com.LetsWork.CRM.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
