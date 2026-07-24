package com.letswork.crm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.letswork.crm.enums.Unit;

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
	    name = "solution_type",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_name_company_id_unit", 
	            columnNames = {"name", "company_id", "unit"}
	        )
	    }
	)
public class SolutionType extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "unit")
    @Enumerated(EnumType.STRING)
    private Unit unit;

}
