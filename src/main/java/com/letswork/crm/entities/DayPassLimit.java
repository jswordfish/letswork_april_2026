package com.letswork.crm.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.letswork.crm.enums.BookingStatus;

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
	    name = "day_pass_limit",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_name_conference", 
	            columnNames = {"lets_work_centre", "company_id"}
	        )
	    }
	)
public class DayPassLimit extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "lets_work_centre")
	private String letsWorkCentre;
	
	private String city;
	
	private String state;
	
	private Integer maxLimit;

}
