package com.letswork.crm.entities;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Amenities extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String name;
	
	private String description;
	
	private String s3Path;

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof Amenities)) return false;
	    Amenities other = (Amenities) o;
	    return id != null && id.equals(other.getId());
	}

	@Override
	public int hashCode() {
	    return getClass().hashCode(); 
	}
	
	

}
