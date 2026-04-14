package com.letswork.crm.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("DayPassBundleBooking")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DayPassBundleBooking extends Booking {

//	@ManyToOne
//	@JoinColumn(name = "day_pass_bundle_id", referencedColumnName = "id") // Add this!
//	private DayPassBundle dayPassBundle;

	/**
	 * Temp fix to avoid nested foreign constraint problem
	 */
	
	@ManyToOne
	LetsWorkCentre letsWorkCentre;
	
	Long dayPassBundleeId;

	private LocalDate expiryDate;

	Integer remainingNumberOfDays;
	
	BigDecimal price;

	@ManyToOne
	Offers appliedOffer;

	Float discountedPrice;

	Boolean paid;

	
	
	

}
