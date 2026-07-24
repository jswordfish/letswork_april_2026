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

import com.letswork.crm.enums.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
	    name = "category",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_name_conference", 
	            columnNames = {"name", "category_type", "company_id"}
	        )
	    }
	)
public class Category extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Enumerated(EnumType.STRING)  
    @Column(nullable = false, name = "category_type")
    private CategoryType categoryType;

}
