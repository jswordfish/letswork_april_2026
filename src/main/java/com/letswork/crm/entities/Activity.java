package com.letswork.crm.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.letswork.crm.enums.ActionType;

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
public class Activity extends Base{
	
	private Long leadId;
	
	private String header;
	
	@Enumerated(EnumType.STRING)
	private ActionType actionType;

}
