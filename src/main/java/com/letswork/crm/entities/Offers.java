package com.letswork.crm.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.letswork.crm.enums.OfferType;

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
	    name = "offers",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_name_offer", 
	            columnNames = {"code", "company_id"}
	        )
	    }
	)
public class Offers extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String name;
	
	@Column(name = "code")
	private String code;
	
	private Integer discount;
	
	private String minDiscountValue;
	
	private LocalDateTime startDate;
	
	private LocalDateTime endDate;
	
	private Boolean active;
	
	@Enumerated(EnumType.STRING)
	private OfferType offerType;
	
	private Boolean deleted;
	
}
