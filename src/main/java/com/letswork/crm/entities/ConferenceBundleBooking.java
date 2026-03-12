package com.letswork.crm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ConferenceBundleBooking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceBundleBooking extends Booking{
	
	
	@ManyToOne
	private ConferenceBundle dayPassBundle;
	
	private Float numberOfHours;
	
	private LocalDate startDate;///this....was not there
	
	private LocalDateTime expiryDate;//this was there
	
	Float price;
	
	Float discountedPrice;

}
