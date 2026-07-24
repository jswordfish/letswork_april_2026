package com.letswork.crm.entities;


import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Table(
	    name = "conference_bundle",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_name_conference_bundle", 
	            columnNames = {"name", "company_id"}
	        )
	    }
	)
public class ConferenceBundle extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "name")
	private String name;
	
	private Float numberOfHours;
	
	private BigDecimal price;
		
	private Integer validForDays;
	
	private Boolean showInApp;
	
	private Boolean freeCredit;

}
