package com.letswork.crm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Table(
	    name = "solutions",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_solution_type_centre", 
	            columnNames = {"solution_type_id", "lets_work_centre"}
	        )
	    }
	)
public class Solutions extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String name;
	
	private String price;
	
	@Column(name = "lets_work_centre", nullable = false)
	private String letsWorkCentre;
	
	private String city;
	
	private String state;
	
	private String s3Path;
	
	private String amenities;
	
	@ManyToOne
	@JoinColumn(name = "solution_type_id")
	private SolutionType solutionType;

}
