package com.letswork.crm.entities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

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
@Table(
	    name = "conference_room",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_name_conference", 
	            columnNames = {"name", "lets_work_centre", "company_id"}
	        )
	    }
	)
public class ConferenceRoom extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    @NotBlank
    private String name;
    
    private Integer capacity;
    
    @Column(name = "lets_work_centre")
    private String letsWorkCentre;
    
    private BigDecimal halfHourPrice;
    
	private String state;
	
	private String city;
		
	private String s3Path;
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
	    name = "conference_room_amenities",
	    joinColumns = @JoinColumn(name = "conference_room_id"),
	    inverseJoinColumns = @JoinColumn(name = "amenity_id")
	)
	private Set<Amenities> amenities = new HashSet<>();
    
}
