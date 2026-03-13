package com.letswork.crm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("DayPassBundle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayPassBundleBooking extends Booking{
	
	
	
	@ManyToOne
	private DayPassBundle dayPassBundle;
	
	private LocalDateTime expiryDate;//this was there
	
	Float price;
	
	@ManyToOne
	Offers appliedOffer;
	
	Float discountedPrice;
	
	Boolean paixd;

}
