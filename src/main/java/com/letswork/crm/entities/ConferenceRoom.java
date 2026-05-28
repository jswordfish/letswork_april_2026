package com.letswork.crm.entities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
public class ConferenceRoom extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;
    
    private Integer capacity;

    private String letsWorkCentre;
    
    private BigDecimal halfHourPrice;
    
	private String state;
	
	private String city;
		
	private String s3Path;
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
	    name = "conference_room_amenities",
	    joinColumns = @JoinColumn(name = "conference_room_id"),
	    inverseJoinColumns = @JoinColumn(name = "amenity_id")
	)
	private Set<Amenities> amenities = new HashSet<>();
    
}
