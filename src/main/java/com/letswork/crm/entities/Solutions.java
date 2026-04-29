package com.letswork.crm.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
@JsonIgnoreProperties(
	    value = {"id", "createDate", "updateDate", "s3Path"},
	    allowGetters = true
	)
public class Solutions extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String name;
	
	private String price;
	
	private String letsWorkCentre;
	
	private String city;
	
	private String state;
	
	private String s3Path;
	
	private String amenities;
	
	@ManyToOne
	@JoinColumn(name = "solution_type_id")
	private SolutionType solutionType;

}
