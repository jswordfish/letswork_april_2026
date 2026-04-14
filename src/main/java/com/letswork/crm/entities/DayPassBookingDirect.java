package com.letswork.crm.entities;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("DayPassBookingDirect")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayPassBookingDirect extends Booking {


	private Integer numberOfPasses;

	@ManyToOne
	LetsWorkCentre letsWorkCentre;

	BigDecimal price;

	@ManyToOne
	Offers appliedOffer;

	BigDecimal discountedPrice;
	
	String qrS3Path;
	
	Long previousBookingId;

}
